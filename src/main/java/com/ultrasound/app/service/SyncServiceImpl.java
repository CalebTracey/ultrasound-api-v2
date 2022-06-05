package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.service.models.SingleFileStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {

    private final ClassificationService classificationService;
    private final  SubMenuService subMenuService;

    // Look at the S3 bucket and make any database changes needed
    @Override
    public MessageResponse synchronize(List<String> files) {
        // clear all the gravestone flags in the DB, so we know which ones to delete at the end
        classificationService.clearGravestones();
        // iterate over the files and create categories, submenus as needed
        StringBuilder builder = new StringBuilder();

        files.forEach(name -> {
            try {
                Optional<SingleFileStructure> fileData;
                fileData = normalizeFileData(name);
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
                    String submenuName = fileData.get().getSubMenuName();
                    if (!classification.getSubMenus().containsKey(submenuName)) {
                        classification = classificationService.addNewSubMenu(classification.get_id(), submenuName);
                    }
                    String subMenuId = classification.getSubMenus().get(submenuName);
                    SubMenu subMenu = subMenuService.getById(subMenuId);
                    subMenu.setGravestone(false);
                    subMenuService.save(subMenu);

                    // does the submenu have the listItem?
                    Predicate<ListItem> linkMatchPredicate = listItem -> listItem.getLink().equals(fileData.get().getScan().getLink());
                    if (subMenu.getItemList().stream().noneMatch(linkMatchPredicate)) {
                        subMenu.getItemList().add(fileData.get().getScan());
                    } else if (subMenu.getItemList().stream().anyMatch(linkMatchPredicate)) {
                        // clear the gravestone
                        Optional<ListItem> linkMatch = subMenu.getItemList().stream().filter(linkMatchPredicate).findFirst();
                        linkMatch.ifPresent(listItem -> listItem.setGraveStone(false));
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

    @NotNull
    public Optional<SingleFileStructure> normalizeFileData(String file) throws ParseException {
        SingleFileStructure fileStructure = new SingleFileStructure();
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
