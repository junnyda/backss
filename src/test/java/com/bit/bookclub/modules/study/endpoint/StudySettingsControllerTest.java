package com.bit.bookclub.modules.study.endpoint;

import com.bit.bookclub.infra.IntegrationTest;
import com.bit.bookclub.modules.account.WithAccount;
import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.account.form.TagForm;
import com.bit.bookclub.modules.account.form.ZoneForm;
import com.bit.bookclub.modules.account.repository.AccountRepository;
import com.bit.bookclub.modules.study.domain.entity.Study;
import com.bit.bookclub.modules.study.form.StudyForm;
import com.bit.bookclub.modules.study.repository.StudyRepository;
import com.bit.bookclub.modules.study.service.StudyService;
import com.bit.bookclub.modules.tag.domain.entity.Tag;
import com.bit.bookclub.modules.tag.infra.repository.TagRepository;
import com.bit.bookclub.modules.zone.repository.ZoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
class StudySettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired StudyService studyService;
    @Autowired ObjectMapper objectMapper;
    private final String studyPath = "study-test";

    @BeforeEach
    void beforeEach() {
        Account account = accountRepository.findByNickname("jaime");
        studyService.createNewStudy(StudyForm.builder()
                .path(studyPath)
                .shortDescription("short-description")
                .fullDescription("full-description")
                .title("title")
                .build(), account);
    }

    @AfterEach
    void afterEach() {
        studyRepository.deleteAll();
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????(??????)")
    @WithAccount("jaime")
    void studySettingFormDescription() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @DisplayName("????????? ?????? ??????: ??????")
    @WithAccount("jaime")
    void updateStudyDescription() throws Exception {
        Account account = accountRepository.findByNickname("jaime");
        String shortDescriptionToBeUpdated = "short-description-test";
        String fullDescriptionToBeUpdated = "full-description-test";
        mockMvc.perform(post("/study/" + studyPath + "/settings/description")
                        .param("shortDescription", shortDescriptionToBeUpdated)
                        .param("fullDescription", fullDescriptionToBeUpdated)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/description"));
        Study study = studyService.getStudyToUpdate(account, studyPath);
        assertEquals(shortDescriptionToBeUpdated, study.getShortDescription());
        assertEquals(fullDescriptionToBeUpdated, study.getFullDescription());
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????(??????)")
    @WithAccount("jaime")
    void studySettingFormBanner() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("????????? ?????? ????????????")
    @WithAccount("jaime")
    void updateStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner")
                        .param("image", "image-test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
    }


    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void enableStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
        Study study = studyRepository.findByPath(studyPath);
        assertTrue(study.useBanner());
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
    @WithAccount("jaime")
    void disableStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner/disable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
        Study study = studyRepository.findByPath(studyPath);
        assertFalse(study.useBanner());
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????(????????? ??????)")
    @WithAccount("jaime")
    void studySettingFormTag() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void addStudyTag() throws Exception {
        String tagTitle = "newTag";
        TagForm tagForm = TagForm.builder()
                .tagTitle(tagTitle)
                .build(); // ???????????? ?????? ?????? ????????? ?????? ?????? TagForm??? @AllArgsConstructor, @Builder ??????
        mockMvc.perform(post("/study/" + studyPath + "/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Study study = studyRepository.findStudyWithTagsByPath(studyPath);
        Tag tag = tagRepository.findByTitle(tagTitle).orElse(null);
        assertNotNull(tag);
        assertTrue(study.getTags().contains(tag));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void removeStudyTag() throws Exception {
        Study study = studyRepository.findStudyWithTagsByPath(studyPath);
        String tagTitle = "newTag";
        Tag tag = tagRepository.save(Tag.builder()
                .title(tagTitle)
                .build());
        studyService.addTag(study, tag);
        TagForm tagForm = TagForm.builder()
                .tagTitle(tagTitle)
                .build(); // ???????????? ?????? ?????? ????????? ?????? ?????? TagForm??? @AllArgsConstructor, @Builder ??????
        mockMvc.perform(post("/study/" + studyPath + "/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(study.getTags().contains(tag));
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????(?????? ??????)")
    @WithAccount("jaime")
    void studySettingFormZone() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void addStudyZone() throws Exception {
        Zone testZone = Zone.builder().city("test").localNameOfCity("????????????").province("????????????").build();
        zoneRepository.save(testZone);
        ZoneForm zoneForm = ZoneForm.builder()
                .zoneName(testZone.toString())
                .build();
        zoneForm.setZoneName(testZone.toString());
        mockMvc.perform(post("/study/" + studyPath + "/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Study study = studyRepository.findStudyWithZonesByPath(studyPath);
        assertTrue(study.getZones().contains(testZone));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void removeStudyZone() throws Exception {
        Study study = studyRepository.findStudyWithZonesByPath(studyPath);
        Zone testZone = Zone.builder().city("test").localNameOfCity("????????????").province("????????????").build();
        zoneRepository.save(testZone);
        studyService.addZone(study, testZone);
        ZoneForm zoneForm = ZoneForm.builder()
                .zoneName(testZone.toString())
                .build();
        mockMvc.perform(post("/study/" + studyPath + "/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(study.getZones().contains(testZone));
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????(?????????)")
    @WithAccount("jaime")
    void studySettingFormStudy() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("????????? ??????")
    @WithAccount("jaime")
    void publishStudy() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(studyPath);
        assertTrue(study.isPublished());
    }

    @Test
    @DisplayName("????????? ??????")
    @WithAccount("jaime")
    void closeStudy() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/close")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isClosed());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    @WithAccount("jaime")
    void startRecruit() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isRecruiting());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????: 1?????? ?????? ?????? -> ??????")
    @WithAccount("jaime")
    void stopRecruit() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        studyService.startRecruit(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isRecruiting());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void updateStudyPath() throws Exception {
        String newPath = "new-path";
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/path")
                        .param("newPath", newPath)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + newPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(newPath);
        assertEquals(newPath, study.getPath());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithAccount("jaime")
    void updateStudyTitle() throws Exception {
        String newTitle = "newTitle";
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/title")
                        .param("newTitle", newTitle)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(studyPath);
        assertEquals(newTitle, study.getTitle());
    }

    @Test
    @DisplayName("????????? ??????")
    @WithAccount("jaime")
    void removeStudy() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        assertNull(studyRepository.findByPath(studyPath));
    }
}