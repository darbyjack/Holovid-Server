package io.github.holovid.server;

import io.github.holovid.server.controller.ResourcePackController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(ResourcePackController.class)
public class ResourcePackTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testDownload() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/resourcepack/download").param("videoUrl", "https://youtube.com/watch?v=dt22yWYX64w"))
                .andExpect(result -> {
                    assert result.getResponse().getStatus() == HttpStatus.OK.value();
                    assert result.getResponse().getContentAsString().endsWith("dt22yWYX64w.zip");
                });
    }

    @Test
    public void testTooLargeDownload() throws Exception {
        // It's... big enough
        mvc.perform(MockMvcRequestBuilders.get("/resourcepack/download").param("videoUrl", "https://www.youtube.com/watch?v=gfkts0u-m6w"))
                .andExpect(result -> {
                    assert result.getResponse().getStatus() == HttpStatus.PAYLOAD_TOO_LARGE.value();
                });
    }

    @Test
    public void testInvalidUrl() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/resourcepack/download").param("videoUrl", "hello owo"))
                .andExpect(result -> {
                    assert result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value();
                });
    }

    @Test
    public void testUnknownSite() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/resourcepack/download").param("videoUrl", "https://www.twitch.tv/smallant/clip/HomelyNimbleBearTriHard"))
                .andExpect(result -> {
                    assert result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value();
                });
    }
}

