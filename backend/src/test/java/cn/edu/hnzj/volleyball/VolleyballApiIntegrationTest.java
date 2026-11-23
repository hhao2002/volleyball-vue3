package cn.edu.hnzj.volleyball;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@Transactional
class VolleyballApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private JsonNode body(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return body(result).path("data").path("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void publicPagesExposeAnnouncementsAndResults() throws Exception {
        mockMvc.perform(get("/api/public/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].pinned").value(1));

        mockMvc.perform(get("/api/public/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].mvpName").value("李盈莹"))
                .andExpect(jsonPath("$.data[0].svpName").value("普拉默"));

        mockMvc.perform(get("/api/public/results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.match.state").value("FINISHED"))
                .andExpect(jsonPath("$.data.mvpName").value("李盈莹"));

        mockMvc.perform(get("/api/live/matches"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void applicationApprovalPromotesUserToScorer() throws Exception {
        MvcResult register = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"newbie123\",\"nickname\":\"新人\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String token = body(register).path("data").path("token").asText();

        mockMvc.perform(post("/api/applications")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"SCORER\",\"reason\":\"希望参与计分\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        String admin = login("admin", "admin123");
        MvcResult list = mockMvc.perform(get("/api/admin/applications")
                        .param("status", "PENDING").param("size", "50")
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andReturn();
        long applicationId = 0;
        for (JsonNode node : body(list).path("data").path("list")) {
            if ("newbie".equals(node.path("applicantUsername").asText())) {
                applicationId = node.path("id").asLong();
            }
        }
        assertTrue(applicationId > 0, "未找到新用户的待处理申请");

        mockMvc.perform(post("/api/admin/applications/" + applicationId + "/approve")
                        .header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"remark\":\"通过\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("SCORER"));

        mockMvc.perform(get("/api/scorer/teams").header("Authorization", bearer(token)))
                .andExpect(status().isOk());
    }

    @Test
    void scoringFlowLocksTeamsAndCreatesMvpSvpAndAnnouncements() throws Exception {
        String scorer = login("scorer", "scorer123");
        String normalUser = login("user", "user123");

        MvcResult created = mockMvc.perform(post("/api/scorer/matches")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"集成测试赛\",\"leftTeamId\":1,\"rightTeamId\":2," +
                                "\"bestOf\":1,\"pointsPerSet\":5,\"finalSetPoints\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.match.state").value("READY"))
                .andReturn();
        long matchId = body(created).path("data").path("match").path("id").asLong();

        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/start")
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.match.state").value("ONGOING"));

        // 比赛进行中，队伍被锁定
        mockMvc.perform(put("/api/scorer/teams/1")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"中国女排\",\"coach\":\"蔡斌\",\"memberIds\":[1,2,3,4,5,6]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("该队伍正在参加进行中的比赛，无法修改"));

        // 普通用户不能计分
        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/points")
                        .header("Authorization", bearer(normalUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1,\"memberId\":1,\"type\":\"ATTACK\"}"))
                .andExpect(status().isForbidden());

        // 1: 朱婷；2: 普拉默；3: 朱婷；4: 李盈莹
        score(scorer, matchId, "{\"teamId\":1,\"memberId\":1,\"type\":\"ATTACK\"}");
        score(scorer, matchId, "{\"teamId\":2,\"memberId\":7,\"type\":\"ATTACK\"}");
        score(scorer, matchId, "{\"teamId\":1,\"memberId\":1,\"type\":\"ATTACK\"}");
        score(scorer, matchId, "{\"teamId\":1,\"memberId\":2,\"type\":\"ATTACK\"}");

        // 撤销后重新计分
        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/undo")
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.match.leftPoints").value(2));

        score(scorer, matchId, "{\"teamId\":1,\"memberId\":2,\"type\":\"ATTACK\"}");
        score(scorer, matchId, "{\"teamId\":1,\"type\":\"OUT\"}");
        score(scorer, matchId, "{\"teamId\":2,\"memberId\":11,\"type\":\"ATTACK\"}");
        score(scorer, matchId, "{\"teamId\":2,\"memberId\":7,\"type\":\"ATTACK\"}");
        MvcResult finish = mockMvc.perform(post("/api/scorer/matches/" + matchId + "/points")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1,\"memberId\":1,\"type\":\"ATTACK\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.match.state").value("FINISHED"))
                .andExpect(jsonPath("$.data.match.leftPoints").value(5))
                .andExpect(jsonPath("$.data.match.rightPoints").value(3))
                .andExpect(jsonPath("$.data.mvpName").value("朱婷"))
                .andExpect(jsonPath("$.data.svpName").value("普拉默"))
                .andReturn();

        JsonNode detail = body(finish).path("data");
        boolean hasOutEvent = false;
        for (JsonNode event : detail.path("events")) {
            if ("OUT".equals(event.path("type").asText())) {
                hasOutEvent = true;
                assertTrue(event.path("memberId").isNull() || event.path("memberId").isMissingNode(),
                        "出界得分不应记录球员");
            }
        }
        assertTrue(hasOutEvent, "缺少出界得分记录");

        // 自动公告：开始 + 结束
        MvcResult announcements = mockMvc.perform(get("/api/public/announcements").param("limit", "50"))
                .andExpect(status().isOk())
                .andReturn();
        boolean hasStart = false;
        boolean hasEnd = false;
        for (JsonNode node : body(announcements).path("data")) {
            if (node.path("matchId").asLong() == matchId && "MATCH_START".equals(node.path("type").asText())) {
                hasStart = true;
            }
            if (node.path("matchId").asLong() == matchId && "MATCH_END".equals(node.path("type").asText())) {
                hasEnd = true;
            }
        }
        assertTrue(hasStart, "比赛开始未生成公告");
        assertTrue(hasEnd, "比赛结束未生成公告");

        // 结束后队伍解锁
        mockMvc.perform(put("/api/scorer/teams/1")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"中国女排\",\"coach\":\"蔡斌\",\"memberIds\":[1,2,3,4,5,6]}"))
                .andExpect(status().isOk());

        // 已结束的比赛不能再计分或撤销
        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/points")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1,\"memberId\":1,\"type\":\"ATTACK\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/undo")
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isBadRequest());

        // 结果公开可查
        mockMvc.perform(get("/api/public/results/" + matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mvpName").value("朱婷"))
                .andExpect(jsonPath("$.data.svpName").value("普拉默"));
    }

    private void score(String token, long matchId, String json) throws Exception {
        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/points")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void teamAndMemberManagementValidatesLineup() throws Exception {
        String scorer = login("scorer", "scorer123");

        // 不足 6 人不能保存
        mockMvc.perform(put("/api/scorer/teams/1")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"中国女排\",\"memberIds\":[1,2,3,4,5]}"))
                .andExpect(status().isBadRequest());

        MvcResult created = mockMvc.perform(post("/api/scorer/teams")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试联队\",\"coach\":\"测试教练\",\"color\":\"#FFAA00\"," +
                                "\"memberIds\":[13,14,15,16,17,18]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.members.length()").value(6))
                .andReturn();
        long teamId = body(created).path("data").path("id").asLong();

        // 重名拒绝
        mockMvc.perform(post("/api/scorer/teams")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试联队\",\"memberIds\":[13,14,15,16,17,18]}"))
                .andExpect(status().isBadRequest());

        // 换人：释放 18，加入 19
        mockMvc.perform(put("/api/scorer/teams/" + teamId)
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试联队\",\"coach\":\"测试教练\"," +
                                "\"memberIds\":[13,14,15,16,17,19]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.members[5].id").value(19));

        mockMvc.perform(get("/api/scorer/members").param("available", "true")
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.id == 18)]").exists());

        // 队员 CRUD
        MvcResult member = mockMvc.perform(post("/api/scorer/members")
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试队员\",\"role\":\"主攻\",\"number\":77}"))
                .andExpect(status().isOk())
                .andReturn();
        long memberId = body(member).path("data").path("id").asLong();
        mockMvc.perform(put("/api/scorer/members/" + memberId)
                        .header("Authorization", bearer(scorer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试队员2\",\"role\":\"副攻\",\"number\":78}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("测试队员2"));
        mockMvc.perform(delete("/api/scorer/members/" + memberId)
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isOk());

        // 未参加比赛的队伍可以删除
        mockMvc.perform(delete("/api/scorer/teams/" + teamId)
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanInspectSystemAndUsers() throws Exception {
        String admin = login("admin", "admin123");

        mockMvc.perform(get("/api/admin/users").param("keyword", "admin")
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("admin"));

        MvcResult system = mockMvc.perform(get("/api/admin/system")
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCount").value(3))
                .andReturn();
        JsonNode info = body(system).path("data");
        assertTrue(info.path("heapMaxMb").asDouble() > 0);
        assertTrue(info.path("cpuCores").asInt() > 0);
        assertNotNull(info.path("osName").asText());

        mockMvc.perform(get("/api/admin/login-logs")
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").isNumber());

        // 普通用户禁止访问管理端
        String user = login("user", "user123");
        mockMvc.perform(get("/api/admin/system").header("Authorization", bearer(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void scorerCanOnlyScoreOwnMatches() throws Exception {
        String admin = login("admin", "admin123");
        String scorer = login("scorer", "scorer123");

        MvcResult created = mockMvc.perform(post("/api/scorer/matches")
                        .header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"管理员创建的比赛\",\"leftTeamId\":1,\"rightTeamId\":2," +
                                "\"bestOf\":3,\"pointsPerSet\":25,\"finalSetPoints\":15}"))
                .andExpect(status().isOk())
                .andReturn();
        long matchId = body(created).path("data").path("match").path("id").asLong();

        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/start")
                        .header("Authorization", bearer(scorer)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("只有创建该比赛的计分员才能操作"));

        mockMvc.perform(post("/api/scorer/matches/" + matchId + "/start")
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
    }
}
