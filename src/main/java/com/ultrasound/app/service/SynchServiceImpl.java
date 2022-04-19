package com.ultrasound.app.service;

import com.ultrasound.app.aws.S3Service;
import com.ultrasound.app.aws.S3ServiceImpl;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SynchServiceImpl implements SynchService {
    @Autowired
    S3Service s3Service;
    @Autowired
    private ClassificationService classificationService;
    @Autowired
    private SubMenuService subMenuService;

    // Look at the S3 bucket and make any database changes needed
    @Override
    public MessageResponse synchronize(List<String> files) {
        // clear all the gravestone flags in the DB so we know which ones to delete at the end
        classificationService.clearGravestones();

        // iterate over the files and create categories, submenus as needed
        StringBuilder builder = new StringBuilder();

        files.forEach(name -> {
            try {
                Optional<SingleFileStructure> fileData = normalizeFileData(name);
                if (fileData.isPresent()) {
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
                    if (!classification.getSubMenus().containsKey(submenuName)) {
                        classification = classificationService.addNewSubMenu(classification.get_id(), submenuName);
                    }


                    String subMenuId = classification.getSubMenus().get(submenuName);
                    SubMenu subMenu = subMenuService.getById(subMenuId);
                    subMenu.setGravestone(false);
                    subMenuService.save(subMenu);

                    // does the submenu have the listItem?
                    Predicate<ListItem> linkMatch = listItem -> listItem.getLink().equals(fileData.get().getScan().getLink());
                    if (subMenu.getItemList().stream().noneMatch(linkMatch)) {
                        subMenu.getItemList().add(fileData.get().scan);
                    } else if (subMenu.getItemList().stream().anyMatch(linkMatch)) {
                        // clear the gravestone
                        subMenu.getItemList().stream().filter(linkMatch).findFirst().get().setGraveStone(false);
                    }
                    subMenuService.save(subMenu);
                }

            } catch (ParseException e) {
                builder.append("Bad file name: ").append(name).append(" Error: ").append(e.getMessage()).append("</br>");
            }
        });

        classificationService.deleteOrphans();

        return new MessageResponse(builder.toString());
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
    }

    @NotNull
    public Optional<SingleFileStructure> normalizeFileData(String file) throws ParseException {
        SynchServiceImpl.SingleFileStructure fileStructure = new SynchServiceImpl.SingleFileStructure();
        String fileNormalized = StringUtils.normalizeSpace(file);
        String[] splitFilePre = StringUtils.split(fileNormalized, "-.");

        //confirm we have all the tokens we need (classification, submenu, listitem, sequence)
        if (splitFilePre.length != 5) {
            throw new ParseException("found " + splitFilePre.length + " elements. Expected 5", splitFilePre.length);
        }

        //confirm good sequence
        int sequence;
        try {
            sequence = Integer.parseInt(splitFilePre[3].trim());
        } catch (NumberFormatException nfe) {
            throw new ParseException("Bad sequence number: " + splitFilePre[3], 3);
        }

        //confirm good filetype
        String extension = splitFilePre[4].toLowerCase();
        ListItem.MediaType mediaType;
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
        fileStructure.setScan(new ListItem(splitFile.get(2), splitFile.get(2), file, sequence, EType.TYPE_ITEM, mediaType, false));
        return Optional.of(fileStructure);
    }

}
