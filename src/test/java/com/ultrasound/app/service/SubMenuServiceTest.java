package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.repo.ClassificationRepo;
import com.ultrasound.app.repo.SubMenuRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
public class SubMenuServiceTest {
    @Mock
    private SubMenuRepo repo;

    private SubMenuServiceImpl s;

    @BeforeEach
    void setUp() {
        SubMenu sm1 = new SubMenu("subMenuid001","foo","subMenu",new ArrayList<>(),EType.TYPE_SUB_MENU,false);
        //ArgumentCaptor<SubMenu> param = ArgumentCaptor.forClass(SubMenu.class);
        Mockito.lenient().when(repo.insert((SubMenu) any())).thenReturn(any()); //ignored in service
//        Mockito.lenient().when(repo.existsByName("foo")).thenReturn(true);
//        Mockito.lenient().when(repo.existsByName("bar")).thenReturn(false);
//        Mockito.lenient().when(repo.findAll()).thenReturn(list);
        Mockito.lenient().when(repo.findById("subMenuid001")).thenReturn( Optional.of(sm1));
        Mockito.lenient().when(repo.save(any())).then(returnsFirstArg());
//        final OngoingStubbing<Object> c2 = Mockito.lenient().when(repo.save(any())).thenReturn(c1);

        s = new SubMenuServiceImpl(repo);
    }


    @Test
    void basicSubMenuFetch() {
        SubMenu sm1 = s.getById("subMenuid001");
        assert sm1.getClassification().equals("foo");
    }

    @Test
    void createNew() {
        SubMenu sm2 = s.createNew("faz","bar");
        assert sm2.getClassification().equals("faz");
    }

    @Test
    void simpleOrphanTest() {
        List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("good1","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,false));
        items.add(new ListItem("good2","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,false));
        items.add(new ListItem("bad1","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,true));

        SubMenu sm3 = s.createNew("foo","bar");
        sm3.setItemList(items);
        Mockito.lenient().when(repo.findById("id03")).thenReturn(Optional.of(sm3));
        Boolean deleted = s.deleteOrphans("id03");
        assert !deleted;
        assert sm3.getItemList().size() == 2;
    }

    @Test
    void allDeadOrphanTest() {
        List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("good1","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,true));
        items.add(new ListItem("good2","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,true));
        items.add(new ListItem("bad1","","",1001,EType.TYPE_ITEM, ListItem.MediaType.VIDEO,true));

        SubMenu sm3 = s.createNew("foo","bar");
        sm3.setItemList(items);
        Mockito.lenient().when(repo.findById("id03")).thenReturn(Optional.of(sm3));
        Boolean deleted = s.deleteOrphans("id03");
        assert deleted;
        assert sm3.getItemList().size() == 0;
    }
}
