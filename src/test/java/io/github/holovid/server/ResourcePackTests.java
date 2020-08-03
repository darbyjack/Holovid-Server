package io.github.holovid.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

    private static final Gson GSON = new Gson();
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private HolovidServerApplication application;
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

                    final String content = result.getResponse().getContentAsString();
                    assert !content.isEmpty();

                    final JsonObject object = GSON.fromJson(content, JsonObject.class);
                    final JsonPrimitive hash = object.getAsJsonPrimitive("hash");
                    assert hash != null && hash.getAsString().length() == 40;

                    final JsonPrimitive url = object.getAsJsonPrimitive("url");
                    assert url != null && url.getAsString().equals(application.getServerUrl() + "downloads/yt/dt22yWYX64w.zip");
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

