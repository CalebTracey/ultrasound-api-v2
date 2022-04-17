package com.ultrasound.app.service;

import com.ultrasound.app.exceptions.ClassificationNotFoundException;
import com.ultrasound.app.exceptions.SubMenuNotFoundException;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.repo.SubMenuRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class SubMenuServiceImpl implements SubMenuService{

    @Autowired
    private SubMenuRepo subMenuRepo;

    @Override
    public SubMenu save(SubMenu subMenu) {
        return subMenuRepo.save(subMenu);
    }

    @Override
    public SubMenu getById(String id) {
        return subMenuRepo.findById(id).orElseThrow(() -> new SubMenuNotFoundException(id));
    }

    @Override
    public String insert(SubMenu subMenu) {
        return subMenuRepo.insert(subMenu).get_id();
    }

    @Override
    public boolean existsById(String id) {
        return subMenuRepo.existsById(id);
    }

    @Override
    public MessageResponse deleteById(String id) {
        SubMenu subMenu = getById(id);
        String name = subMenu.getName();

        int count = subMenu.getItemList().size();
        log.info("Deleting Submenu {} and {} listItems",name, count);
        subMenuRepo.delete(subMenu);

        return new MessageResponse("Deleted submenu " + name + " and " + count + " list items");
    }

    @Override
    public SubMenu createNew(String classificationId, String name) {
        SubMenu newSubMenu = new SubMenu(null,classificationId,name,new ArrayList<>(), EType.TYPE_SUB_MENU, false);
        //new SubMenu(name, new ArrayList<>(), EType.TYPE_SUB_MENU);
        return save(newSubMenu);
    }

    @Override
    public void deleteTableEntities() {
        subMenuRepo.deleteAll();
    }

    // clear the gravestones for the submenu and all its items
    @Override
    public void clearGravestones() {
        List<SubMenu> subMenus = subMenuRepo.findAll();
        subMenus.forEach((s) -> {
            s.setGravestone(true);
            s.getItemList().forEach((i) -> i.setGraveStone(true));
            save(s);
        });
    }

    /**
     * Remove any scans from this subMenu that have their gravestone still set.
     * Also deletes the subMenu itself if its gravestone is still set.
     * @return Whether the subMenu itself was deleted
     */
    //
    @Override
    public Boolean deleteOrphans(String subMenuId) {

        SubMenu subMenu = getById(subMenuId);
        int count = subMenu.getItemList().size();

        Predicate<ListItem> touched = ListItem -> ListItem.getGraveStone().equals(false);
        List<ListItem> newList = subMenu.getItemList().stream().filter(touched).collect(Collectors.toList());

        subMenu.setItemList(newList);
        save(subMenu);
        if (count - subMenu.getItemList().size() > 0) {
            log.info("Deleted {} scans", count - subMenu.getItemList().size());
        }

        //also check if the submenu needs deleting
        if (subMenu.getGravestone() || subMenu.getItemList().size() == 0) {
            deleteById(subMenuId);
            log.info("deleted subMenu {}", subMenuId );
            return true; // the classification also needs to be updated to remove reference to this subMenu
        }

        return false;
    }
}
