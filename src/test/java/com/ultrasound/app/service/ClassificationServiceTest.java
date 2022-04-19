package com.ultrasound.app.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.repo.ClassificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
public class ClassificationServiceTest {
    @Mock
    private ClassificationRepo repo;

    @Mock
    private SubMenuService subMenuService;

    private ClassificationServiceImpl s;

    @BeforeEach
    void setUp() {
        SubMenu sm1 = new SubMenu("subMenuid001","foo","subMenu",new ArrayList<>(),EType.TYPE_SUB_MENU,false);
        Map<String,String> subMenus = new HashMap<>();
        subMenus.put("subMenu","subMenuid001");
        Classification c1 = new Classification("id001","foo",true,subMenus,EType.TYPE_CLASSIFICATION,false);
                //new Classification("foo",false,new ArrayList<>(),new HashMap<>(), EType.TYPE_CLASSIFICATION);
        List<Classification> list = new ArrayList<>();
        list.add(c1);

        SubMenu s1 = new SubMenu("sub1id","id001","sub1",new ArrayList<>(),EType.TYPE_SUB_MENU,false);
        //new SubMenu("sub1", new ArrayList<>());

        Mockito.lenient().when(repo.insert((Classification) any())).thenReturn(c1); //ignored in service
        Mockito.lenient().when(repo.existsByName("foo")).thenReturn(true);
        Mockito.lenient().when(repo.existsByName("bar")).thenReturn(false);
        Mockito.lenient().when(repo.findAll()).thenReturn(list);
        Mockito.lenient().when(repo.findById("id001")).thenReturn( Optional.of(c1));
        final OngoingStubbing<Object> c2 = Mockito.lenient().when(repo.save(any())).thenReturn(c1);

//        Mockito.lenient().when(subMenuService.getById(anyString())).thenReturn(s1);
//        Mockito.lenient().when(subMenuService.deleteOrphans(any())).thenReturn(null);
//        Mockito.lenient().when(subMenuService.existsById(any())).thenReturn(false);
//        Mockito.lenient().when(subMenuService.existsById("sub1")).thenReturn(true);
//        Mockito.lenient().when(subMenuService.deleteById("sub1")).thenReturn(null);
//        Mockito.lenient().when(subMenuService.save((SubMenu) any())).thenReturn(any());

        s = new ClassificationServiceImpl(repo, subMenuService);
    }

    @Test
    void basicTest() {
        Classification c1 = s.getById("id001");
        assert c1 != null;
        assert c1.getName().equals("foo");
    }

    @Test
    void basicSubMenuFetch() {
        Classification c1 = s.getById("id001");
        assert s.getById("id001").getHasSubMenu();
        assert s.getById("id001").getSubMenus().containsKey("subMenu");
    }

//    @Test
//    void addSubMenu() {
//        Classification c1 = s.getById("id001");
//        Classification c2 = s.addNewSubMenu(c1.get_id(), "newSubMenu");
//        assert c2.getSubMenus().keySet().contains("newSubMenu");
//    }
}
