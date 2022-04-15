package com.ultrasound.app.aws.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.*;

import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.service.ClassificationServiceImpl;
import com.ultrasound.app.service.SubMenuServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class S3UtilsImpl implements S3Utils {

    private final AmazonS3 s3Client;

    @Autowired
    private ClassificationServiceImpl classificationService;

    @Autowired
    Environment env;

    public String getAWSBucketName() {
        return env.getProperty("aws.bucket.name");
    }

    //private SubMenuServiceImpl subMenuService;

    private ListObjectsV2Result listObjectsV2() {
        return s3Client.listObjectsV2(getAWSBucketName());
    }

    public S3UtilsImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }
//
//    public void changeS3FileName(String newKey, String oldKey) {
//        pushToBucket(newKey, oldKey);
//        s3Client.deleteObject(new DeleteObjectRequest(getAWSBucketName(), oldKey));
//        log.info("changed filename \"{}\" to \"{}\"", newKey, oldKey);
//    }
//
//    @Override
//    public MessageResponse cleanBucketFileExtensions() {
//        AtomicInteger count = new AtomicInteger();
//
//        listObjectsV2().getObjectSummaries().forEach(objectSummary -> {
//            if (!usableExtensions(objectSummary)) {
//                s3Client.deleteObject(
//                        new DeleteObjectRequest(getAWSBucketName(), objectSummary.getKey()));
//                count.getAndIncrement();
//            }
//        });
//        return new MessageResponse("Deleted " + count + " non-mp4 files");
//    }
//
//    @Override
//    public MessageResponse updateFileNamesAndExport() {
//        AtomicInteger fileCount = new AtomicInteger();
//        classificationService.all().forEach(classification -> {
//            if (classification.getHasSubMenu()) {
//                classificationService.subMenuObjects(classification.getSubMenus())
//                        .forEach(subMenu -> {
//                    subMenu.getItemList().forEach(listItem -> {
//                        List<String> fileParts = new ArrayList<>(List.of(classification.getName(), subMenu.getName(), listItem.getName()));
//                        fileParts.add(".mp4");
//                        String newFileName = StringUtils.join(finalizeScanTitleParts(fileParts), " - ");
//                        pushToBucket(newFileName, listItem.getLink());
//                        fileCount.getAndIncrement();
//                    });
//                });
//            }
//            classification.getListItems().forEach(listItem -> {
//                String newFileName = StringUtils.join(new String[]{
//                        classification.getName(), listItem.getName(), ".mp4"}, " - ");
//                pushToBucket(newFileName, listItem.getLink());
//                fileCount.getAndIncrement();
//            });
//        });
//
//        return new MessageResponse("Exported " + fileCount + " files");
//    }

//    private void pushToBucket(String newKey, String oldKey) {
//        S3Object oldFile = s3Client.getObject(getAWSBucketName(), oldKey);
//        String EXPORT_BUCKET_NAME = "ultrasound-files-export";
//        s3Client.putObject(EXPORT_BUCKET_NAME, newKey, oldFile.getObjectContent().getDelegateStream(), oldFile.getObjectMetadata());
//    }

    private @NotNull Boolean usableExtensions(@NotNull S3ObjectSummary objectSummary) {
        return FilenameUtils.isExtension(objectSummary.getKey(), "mp4");
    }

    private List<String> finalizeScanTitleParts(@NotNull List<String> titleParts) {
        return titleParts.stream().map(titlePart -> {
            String returnVal = titlePart;
            if (StringUtils.containsWhitespace(titlePart)) {
                Predicate<String> numberPredicate = String -> NumberUtils.isDigits(String) && String.length() == 7;
                Predicate<String> patientId = String -> StringUtils.startsWith(String,"e") &&
                        !NumberUtils.isCreatable(String.substring(1, StringUtils.length(String)));
                List<String> titlePartArray = Arrays.stream(StringUtils.split(StringUtils.trim(titlePart), " "))
                        .filter(Predicate.not(numberPredicate).or(patientId)).collect(Collectors.toList());
                returnVal = StringUtils.join(titlePartArray, " ");
            }
            return returnVal;
        }).collect(Collectors.toList());
    }

}
