package com.ultrasound.app.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.service.ClassificationServiceImpl;
import com.ultrasound.app.service.SubMenuService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@Configuration
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Autowired
    private ClassificationServiceImpl classificationService;
    @Autowired
    private SubMenuService subMenuService;
    @Autowired
    Environment env;

    public S3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    private String getAWSBucketName() {
        return env.getProperty("aws.bucket.name");
    }

    // Look at the S3 bucket and make any database changes needed
    public MessageResponse synchronize() {
        // clear all the gravestone flags in the DB so we know which ones to delete at the end
        classificationService.clearGravestones();
        subMenuService.clearGravestones();

        List<String> files = getFileNames();
        // iterate over the files and create categories, submenus as needed
        StringBuilder builder = new StringBuilder();

        files.forEach(name -> {
            try {
                Optional<SingleFileStructure> fileData = normalizeFileData(name);

                // does the classification already exist?
                if (!classificationService.classificationExists(fileData.get().getClassification())) {
                    // if not, create it
                    classificationService.createNew(fileData.get().getClassification());
                }

                Classification classification = classificationService.getByName(fileData.get().getClassification());
                classification.setGravestone(false);
                classificationService.save(classification);

                // does the subMenu exist?
                String submenuName = fileData.get().subMenuName;
                String submenuId = null;
                if (!classification.getSubMenus().containsKey(submenuName)) {
                    subMenuService.createNew(classification.get_id(),submenuName);
                    classification = classificationService.getByName(fileData.get().getClassification());
                }


                String subMenuId = classification.getSubMenus().get(submenuName);
                SubMenu subMenu = subMenuService.getById(subMenuId);
                subMenu.setGravestone(false);
                subMenuService.save(subMenu);

                // does the submenu have the listItem?
                Predicate<ListItem> linkMatch = listItem -> listItem.getLink().equals(fileData.get().getScan().getLink());
                if (subMenu.getItemList().stream().noneMatch(linkMatch)) {
                    subMenu.getItemList().add(fileData.get().scan);
                } else {
                    // clear the gravestone
                    subMenu.getItemList().stream().filter(linkMatch).findFirst().get().setGraveStone(false);
                }
                subMenuService.save(subMenu);

            } catch (ParseException e) {
                builder.append("Bad file name: ").append(name).append(" Error: ").append(e.getMessage()).append("</br>");
            }
        });

        classificationService.deleteOrphans();

        return new MessageResponse(builder.toString());
    }

    private @NotNull List<S3ObjectSummary> s3FileNames() {
        List<S3ObjectSummary> objectListing = s3ListObjects();
        log.info("Object listing count: {}", objectListing.size());
        return objectListing.stream()
                .filter(this::filterExtensions).collect(Collectors.toList());
    }

    // For files that get deleted out of S3, we need a mechanism to see if we also need
    // to delete them out of the database. This class keeps a flag so we can do a sweep at
    // the end of our processing to see if there are any entries that need deleting.

    @Override
    public List<String> getFileNames() {
        return s3FileNames().stream().map(S3ObjectSummary::getKey)     // keys = title of file
                .collect(Collectors.toList());
    }

    /**
     * Utility method that takes a single file name as an input and creates a new
     * SingleFileStructure object.
     *
     * @param file The original file name. Also the key for file in S3 Bucket.
     * @return A new SingleFileStructure object.
     */
    private @NotNull Optional<SingleFileStructure> normalizeFileData(String file) throws ParseException {
        SingleFileStructure fileStructure = new SingleFileStructure();
        String fileNormalized = StringUtils.normalizeSpace(file);
        String[] splitFilePre = StringUtils.split(fileNormalized, "-.");

        //confirm we have all the tokens we need (classification, submenu, listitem, sequence)
        if (splitFilePre.length != 5) {
            throw new ParseException("found " + splitFilePre.length + " elements. Expected 5", splitFilePre.length);
        }

        //confirm good sequence
        int sequence = 0;
        try {
            sequence = Integer.parseInt(splitFilePre[3].trim());
        } catch (NumberFormatException nfe) {
            throw new ParseException("Bad sequence number: " + splitFilePre[3], 3);
        }

        //confirm good filetype
        String extension = splitFilePre[4].toLowerCase();
        ListItem.MediaType mediaType = null;
        if (extension.equals("mp4")) {
            mediaType = ListItem.MediaType.VIDEO;
        } else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png")) {
            mediaType = ListItem.MediaType.IMAGE;
        } else {
            throw new ParseException("Bad file extension " + extension, 4);
        }

        List<String> splitFile =
                Arrays.stream(splitFilePre).map(StringUtils::trim).collect(Collectors.toList());

        fileStructure.setClassification(splitFile.get(0)); // set Classification name
        fileStructure.setSubMenuName(splitFile.get(1));
        fileStructure.setScan(new ListItem(splitFile.get(2),splitFile.get(2),file,sequence,EType.TYPE_ITEM,mediaType,false));
        return Optional.of(fileStructure);
    }

    @Override
    public @NotNull Optional<String> getPreSignedUrl(String link) {
        log.info("Getting pre-signed URL for: {}", link);
        LocalDateTime date = LocalDateTime.now().plusSeconds(100);
        String presignedUrl = null;
        try {
            presignedUrl = s3Client.generatePresignedUrl(getAWSBucketName(), link, date.toDate()).toString();
        } catch (AmazonS3Exception e) {
            log.error("AmazonS3Exception: {}", e.getErrorMessage());
            e.getStackTrace();
        }
        assert presignedUrl != null;
        return Optional.of(presignedUrl);
    }

    public List<S3ObjectSummary> s3ListObjects() {
        List<S3ObjectSummary> summaries = new ArrayList<>();
        try {
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(getAWSBucketName());
        ListObjectsV2Result result = s3Client.listObjectsV2(request);
        summaries.addAll(result.getObjectSummaries());
        while (result.isTruncated()) {
            result = s3Client.listObjectsV2(request);
            summaries.addAll(result.getObjectSummaries());
            String token = result.getNextContinuationToken();
            request.setContinuationToken(token);
        }

        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException: {}", e.getErrorMessage());
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            log.error("SdkClientException: {}", e.getLocalizedMessage());
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return summaries;
    }

    private @NotNull Boolean filterExtensions(@NotNull S3ObjectSummary objectSummary) {
        String[] extensions = new String[]{"mp4", "*.mp4","jpg", "*.jpg", "jpeg", "*.jpeg", "gif", "*.gif", "png", "*.png"};
        return FilenameUtils.isExtension(objectSummary.getKey(), extensions);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static
    class SingleFileStructure {
        private @NotNull String classification;
        private String subMenuName;
        private ListItem scan;
        private String link;
        private Boolean hasSubMenu;
        private Boolean hasScan;

        public SingleFileStructure(@NotNull String classification, ListItem scan, String link, Boolean hasSubMenu, Boolean hasScan) {
            this.classification = classification;
            this.scan = scan;
            this.link = link;
            this.hasSubMenu = hasSubMenu;
            this.hasScan = hasScan;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static
    class FileStructureSubMenu {
        private String classification;
        private String name;
        private List<ListItem> itemList;
    }

}

