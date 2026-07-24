package com.example.quranfx;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.Paint;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.*;

import java.awt.*;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.controlsfx.control.ToggleSwitch;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import javax.imageio.ImageIO;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_RGB32;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_RGB32_1;


public class HelloApplication extends Application {
    private String chapters_string;

    private String basic_prompt = "Create a 9:16 image based on this. Do not portray god or any human or add any text.";

    private int number_of_prompts_per_minute = 5;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ArrayList<Verse_class_final> ayats_processed = new ArrayList<>();

    private int chat_gpt_processed_ayats = 0;
    private int selected_verse = 0;
    private String surat_name_selected = "Al-Fatiha";
    private Media media;
    private MediaPlayer mediaPlayer;
    private String sound_path = "";
    private Stage main_stage;

    private int number_of_audio_channels = 2;
    private boolean did_this_play_already = false;
    private int audio_frequncy_of_the_sound = 44100;
    private double max_hight_of_top_pane_fourth_screen = 0;
    private double max_hight_of_bottom_pane_fourth_screen = 0;
    private ContextMenu empty_tile_pane_context_menu = null;
    private HashMap<String, Media_pool> hashMap_with_media_pool_items = new HashMap<>();
    private Shape_object_time_line last_seen_image_vid_is_playing = null;
    private Image blacked_out_image;
    private BufferedImage blacked_out_image_four_k;
    private Image blacked_out_image_whitened;
    private String quran_token;
    //private long token_expiry;
    private ChangeListener<Number> heightListener_to_scene_for_logo_at_start;
    private HashMap<String, ArrayList<Integer>> hash_map_with_the_translations = new HashMap<>();
    private HashMap<Integer, String> hashMap_id_to_language_name_text = new HashMap<>();
    private Sound_mode sound_mode;
    private ArrayList<Listener_info> array_list_with_all_of_the_image_control_listeners = new ArrayList<>();
    private Stage learn_more_about_app_stage;
    private Stage app_settings_information_stage;
    private Stage render_video_dialogue_stage;
    private WritableImage cached_text_image;
    private HashMap<String, Long> analytics_cool_down_hashmap = new HashMap<>();
    private long last_text_update_time = 0;

    private final static String help_email = "sabrlyhelp@gmail.com";
    private final static String discord_invite_link = "https://discord.gg/ZzPgNx8U95";
    private final static String app_name = "Sabrly";
    private final static String app_version = "1.0";
    private final static String clientId_pre_live = Quran_api_secrets.clientId_pre_live;
    private final static String clientSecret_pre_live = Quran_api_secrets.clientSecret_pre_live;
    private final static String clientId_live = Quran_api_secrets.clientId_live;
    private final static String clientSecret_live = Quran_api_secrets.clientSecret_live;
    private final static Live_mode live_or_pre_live_quran_api = Live_mode.LIVE;
    private final static Running_mode running_mode = Running_mode.PRODUCTION;
    private final static int max_rectangle_width = 16384 /*16384*/;

    private final static int image_view_in_tile_pane_width = 90;
    private final static int image_view_in_tile_pane_height = 160;
    private final static double blurry_circle_raduis = 30D;
    private final static int play_button_size = 30;
    private final static int pause_button_size = 33;
    private final static int min_rectnagle_width = 3;
    private final static int next_prev_button_size = 26;
    private final static int next_prev_circle_size = 50;
    private final static double screen_width_multiplier = 0.55D;
    private final static double screen_height_multiplier = 0.55D;
    private final static int scroll_pane_hosting_time_line_border_width = 1;
    private final static int list_view_languages_border_width = 1;
    private final static int how_long_does_it_take_for_tool_tip_to_show_up = 200;
    private final static int number_of_seconds_of_quran_token_pre_fire = 120;
    private final static int width_and_height_of_arrow_image_view_translation = 20;
    private final static int width_and_height_of_the_control_buttons = 20;
    private final static double multiplier_for_the_icons_inside_the_buttons_audio_control = 1.25D;
    private final static double plus_minus_font_increments = 1;
    private final static String hex_ripple_coulour_for_jfx_buttons = "#808080";
    private final static String no_image_found = "black";
    private final static String unit_sign_beside_opacity = "%";
    private final static String unit_sign_beside_fade_in_fade_out = "s";
    private final static double distance_for_resize_arrow_text_on_cavnas = 15D;
    private final static boolean debug_mode = false;


    //private final static double circle_button_radius = 20D;

    private long lastKnownMediaTime = 0;
    private long lastKnownSystemTime = 0;
    private AnimationTimer timer = null;
    private Last_shown_Image id_of_the_last_image = new Last_shown_Image("", Type_of_Image.FULL_QUALITY);


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getBounds().getWidth() * screen_width_multiplier, Screen.getPrimary().getBounds().getHeight() * screen_height_multiplier);
        //scene.getStylesheets().add(PrimerLight.class.getResource("primer-light.css").toExternalForm());
        //stage.setTitle("السلام عليكم ورحمة الله وبركاته");
        stage.setTitle(app_name);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();
        main_stage = stage;
        HelloController helloController = fxmlLoader.getController();
        set_the_logo_at_the_start(helloController);
        center_the_progress_indicator(helloController, scene);
        center_the_rendering_progress_indicator(helloController, scene);
        listen_to_height_change_property(helloController, scene);
        get_the_quran_api_token(helloController, true, scene);
        set_up_icon_for_settings_mark_button_on_loading_screen(helloController);
        set_the_cursor_for_question_mark_button_on_loading_screen(helloController);
        listen_to_settings_button_clicked_loading_screen(helloController);
        add_app_icon_to_title_bar(stage);
    }


    private void everything_to_be_called_at_the_start(HelloController helloController, Scene scene) {
        if (debug_mode) {
            CrashLog.install();
        }
        make_the_first_real_screen_visible(helloController);
        listen_to_surat_choose(helloController);
        dalle_spinner_listener(helloController);
        ratio_spinner_listen(helloController);
        listen_to_previous_button_clicked(helloController);
        listen_to_next_button_clicked(helloController);
        watch_the_advanced_settings_box(helloController);
        set_up_the_which_chat_gpt_to_use(helloController);
        set_the_width_of_which_gpt_spinner(helloController);
        set_up_the_which_image_spinner(helloController);
        set_the_width_of_image_size(helloController);
        set_up_the_style_spinner(helloController);
        set_the_width_of_the_style_spinner(helloController);
        set_up_the_quality_spinner(helloController);
        set_the_width_of_the_quality_spinner(helloController);
        set_the_height_of_text_prompt_text_area(helloController);
        //next_photo_click_listen(helloController);
        //previous_photo_click_listen(helloController);
        /*listen_to_play(helloController);*/
//        listen_to_full_screen_button(helloController);
        listen_to_genereate_chat_gpt_checkbox(helloController);
        upload_sound_listen(helloController);
        //listen_to_cancel_button_third_screen(helloController);
        get_all_of_the_recitors(helloController);
        listen_to_the_recitor_list_view_click(helloController);
        make_temp_dir();
        clear_temp_directory();
        set_the_icons(helloController);
        listen_to_enter_click_on_select_surat_listview(helloController);
        listen_to_upload_media_button(helloController);
        set_the_width_of_the_left_and_right(helloController);
        listen_to_top_and_bottom_pane_size_change_fourth_screen(helloController, scene);
        listen_to_whole_screen_resize(scene, helloController);
        listen_to_tile_pane_size_change(helloController);
        listen_to_give_feed_back_button(helloController);
//        listen_to_chatgpt_image_view_on_mouse_enetered_and_left(helloController);
        play_the_sound_on_chatgpt_image_clicked(helloController);
//        set_up_glossy_pause_button(helloController);
//        make_the_blurry_chatgpt_image_always_have_the_same_size_as_non_blurry(helloController);
//        listen_to_blurry_image_view_size_change_and_center_the_clip(helloController);
//        bind_play_button_to_circle(helloController);
//        make_play_button_circle(helloController);
        /*add_tool_tip_to_next_verse(helloController);
        add_tool_tip_to_previous_verse(helloController);*/
        write_the_black_image_and_the_whitened_black_image();
        black_out_the_image_view_at_the_start(helloController);
        bind_scroll_pane_height_to_canvas(helloController);
        listen_to_canvas_size_change(helloController);
        set_the_width_of_the_border_of_scroll_pane(helloController);
        scroll_pane_time_line_h_vlaue_listener(helloController);
        set_the_time_line_over_laying_pane_margin(helloController);
        bind_scroll_pane_view_port_to_pane_over_laying_time_line(helloController);
        clip_the_over_the_over_laying_pane(helloController);
        listen_to_pane_over_time_line_being_resized(helloController);
        ignore_scroll_for_overlaying_pane_for_time_line(helloController);
        make_list_view_selection_model_null(helloController);
        add_the_css_files_at_the_start(scene);
        set_the_border_width_of_list_view_languages(helloController);
        //bind_the_languages_list_view_to_the_right_border_pane(helloController);
        listen_to_fast_rewind_button_click(helloController);
        set_rewind_button_click(helloController);
        play_pause_button_click_listen(helloController);
        listen_to_forward_button_click(helloController);
        fast_forward_button_click(helloController);
        bind_question_mark_beside_check_box_image(helloController);
        set_up_help_spread_app_learn_more_button(helloController);
        listen_to_learn_more_spread_app_button(helloController);
        bind_the_opacaity_canvas_to_image_view(helloController);
        set_the_width_and_color_of_image_control_stack_pane(helloController);
        listen_to_help_app_spread_check_mark(helloController);
        set_the_fake_width_opacity(helloController);
        set_the_fake_width_in_image_controls(helloController);
        set_the_opacity_initially(helloController);
        set_the_fade_in_fade_out_initially(helloController);
        set_the_fade_in_fade_in_initially(helloController);
        listen_to_mouse_clicked_inside_image_view_pane(helloController);
        listen_to_render_video_button_clicked(helloController);
        listen_to_help_app_settings_button(helloController);
        bind_the_height_for_settings_icon_to_help_spread_app_button(helloController);
        set_up_the_icon_for_settings_beside_help_spread_app(helloController);
        set_up_the_fade_in_fade_out_slider_ticks(helloController);
        hide_the_settings_mark_button_loading_screen_when_done(helloController);
        set_up_the_text_position_canvas(helloController);
        create_the_split_button(helloController);
        add_split_verse_button_tooltip(helloController);
        /*add_fast_reverse_tool_tip(helloController);
        add_reverse_tool_tip(helloController);
        add_play_pause_tool_tip(helloController);
        add_forward_tool_tip(helloController);
        add_fast_forward_tool_tip(helloController);*/
        listen_to_split_verse(helloController);
        set_the_buttons_color_change_when_hovered(helloController);
        close_everything_on_close(main_stage);
        set_up_the_verses_canvas(helloController);
        send_analytics_event("app_launched");
        check_if_this_is_the_first_launch_and_send_an_event_if_so();
        listen_to_mouse_leaving_and_entering_time_line(helloController);
    }

    /*public static void main(String[] args) {
        launch();
    }*/


    private Request call_chapters_api(HelloController helloController, Scene scene) {
        Request request;
        if (live_or_pre_live_quran_api.equals(Live_mode.LIVE)) {
            request = new Request.Builder()
                    .url("https://apis.quran.foundation/content/api/v4/chapters")
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("x-auth-token", quran_token)
                    .addHeader("x-client-id", clientId_live)
                    .build();
        } else {
            request = new Request.Builder()
                    .url("https://apis-prelive.quran.foundation/content/api/v4/chapters")
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("x-auth-token", quran_token)
                    .addHeader("x-client-id", clientId_pre_live)
                    .build();
        }
        return request;
        /*try {
            Response response = client.newCall(request).enqueue(new );
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP code: " + response.code()
                        + " - " + (response.body() != null ? response.body().string() : ""));
            }
            chapters_string = response.body().string();
            add_surats_to_the_list_view(helloController, chapters_string);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


        /*HttpUrl httpurl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.quran.com")
                .addPathSegment("api")
                .addPathSegment("v4")
                .addPathSegment("chapters")
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(httpurl)
                .method("GET", null)
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            chapters_string = response.body().string();
            add_surats_to_the_list_view(helloController, chapters_string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    private void call_verses_api(HelloController helloController, int id, int page, HashMap<String, ArrayList<String>> hashMap_with_all_of_the_translations_of_verses) {
        String host;
        String client_id;
        if (live_or_pre_live_quran_api.equals(Live_mode.LIVE)) {
            host = "apis.quran.foundation";
            client_id = clientId_live;
        } else {
            host = "apis-prelive.quran.foundation";
            client_id = clientId_pre_live;
        }
        HttpUrl httpurl = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("content")
                .addPathSegment("api")
                .addPathSegment("v4")
                .addPathSegment("verses")
                .addPathSegment("by_chapter")
                .addPathSegment(String.valueOf(id + 1))
                .addQueryParameter("language", "en")
                .addQueryParameter("translations", return_the_translation_string())
                .addQueryParameter("translation_fields", "text")
                .addQueryParameter("per_page", "50")
                .addQueryParameter("page", String.valueOf(page))
                .addQueryParameter("fields", "text_uthmani,audio")
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(httpurl)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("x-auth-token", quran_token)
                .addHeader("x-client-id", client_id)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String verses_string = response.body().string();
            add_all_of_the_verses_to_the_list_after(helloController, verses_string, hashMap_with_all_of_the_translations_of_verses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void add_surats_to_the_list_view(HelloController helloController, String response_string) {
        JsonNode nameNode = return_name_node(response_string);
        if (nameNode != null && nameNode.get("chapters") != null) {
            for (int i = 0; i < nameNode.get("chapters").size(); i++) {
                helloController.choose_the_surat.getItems().add(String.valueOf(nameNode.get("chapters").get(i).get("id")).concat(" - ").concat(String.valueOf(nameNode.get("chapters").get(i).get("name_simple"))).replace("\"", ""));
            }
        } else {
            show_alert("There was a problem getting the list of chapters. Please try again later.");
            try {
                throw new Exception("'chapters' field is missing or null in JSON data.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void listen_to_surat_choose(HelloController helloController) {
        helloController.choose_the_surat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (helloController.choose_the_surat.getSelectionModel().getSelectedIndex() != -1) {
                    helloController.choose_surat_screen.setVisible(false);
                    helloController.choose_ayat_screen.setVisible(true);
                    set_up_second_screen(helloController, helloController.choose_the_surat.getSelectionModel().getSelectedIndex());
                    send_surat_analytics_event(helloController.choose_the_surat.getSelectionModel().getSelectedIndex() + 1);
                    send_analytics_event("second_screen_opened");
                }
            }
        });
    }

    private void set_up_second_screen(HelloController helloController, int id) {
        JsonNode nameNode = return_name_node(chapters_string);
        String surat_name = String.valueOf(nameNode.get("chapters").get(id).get("name_simple"));
        String arabic_surat_name = String.valueOf(nameNode.get("chapters").get(id).get("name_arabic"));
        int verse_count = Integer.parseInt(String.valueOf(nameNode.get("chapters").get(id).get("verses_count")));
        int revelation_order = Integer.parseInt(String.valueOf(nameNode.get("chapters").get(id).get("revelation_order")));
        String revelation_place = String.valueOf(nameNode.get("chapters").get(id).get("revelation_place"));
        ArrayNode arrayNode = (ArrayNode) nameNode.get("chapters").get(id).get("pages");
        int number_of_pages = Integer.parseInt(String.valueOf(arrayNode.get(1))) - Integer.parseInt(String.valueOf(arrayNode.get(0))) + 1;
        helloController.show_surat_name.setText(surat_name);
        helloController.show_surat_name_arabic.setText(arabic_surat_name);
        set_the_number_of_verses(helloController, verse_count);
        set_the_extra_information(helloController, surat_name, revelation_order, revelation_place, verse_count, number_of_pages);
        surat_name_selected = surat_name;
        set_up_first_and_last_ayat_combobox(helloController, verse_count);
        initialize_the_combo_box_start_ayat(helloController);
    }

    private void set_the_number_of_verses(HelloController helloController, int number_of_verses) {
        if (number_of_verses == 1) {
            helloController.number_of_ayats.setText("1 verse");
        } else {
            helloController.number_of_ayats.setText(String.valueOf(number_of_verses).concat(" verses"));
        }
    }

    private void set_the_extra_information(HelloController helloController, String surat_name, int revelation_order, String revelation_place, int verses_count, int number_of_pages) {
        String revelation_order_string;
        if (revelation_order == 11 || revelation_order == 12 || revelation_order == 13) {
            revelation_order_string = String.valueOf(revelation_order).concat("th");
        } else {
            if (revelation_order % 10 == 1) {
                revelation_order_string = String.valueOf(revelation_order).concat("st");
            } else if (revelation_order % 10 == 2) {
                revelation_order_string = String.valueOf(revelation_order).concat("nd");
            } else if (revelation_order % 10 == 3) {
                revelation_order_string = String.valueOf(revelation_order).concat("rd");
            } else {
                revelation_order_string = String.valueOf(revelation_order).concat("th");
            }
        }
        revelation_place = revelation_place.replace("\"", "");
        revelation_place = revelation_place.substring(0, 1).toUpperCase() + revelation_place.substring(1);
        String pages;
        if (number_of_pages == 1) {
            pages = "1 page.";
        } else {
            pages = String.valueOf(number_of_pages).concat(" pages.");
        }
        helloController.show_information_about_surat.setText("Surat ".concat(surat_name).concat(" is the ").concat(revelation_order_string).concat(" revelation of the Quran and was revealed in ").concat(revelation_place).concat(". It contains ").concat(String.valueOf(verses_count)).concat(" verses and consists of ").concat(pages));
    }


    private JsonNode return_name_node(String response_string) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode nameNode = mapper.readTree(response_string);
        return nameNode;
    }

    private boolean is_the_ayat_format_correct(HelloController helloController) {
        int start_ayat = helloController.combobox_to_choose_starting_ayat.getSelectionModel().getSelectedItem();
        int end_ayat = helloController.combobox_to_choose_last_ayat.getSelectionModel().getSelectedItem();
        if (end_ayat >= start_ayat) {
            return true;
        } else {
            return false;
        }
    }

    private void show_alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        alert.initOwner(main_stage);
        alert.setTitle("Error!");
        alert.setHeaderText("An error has occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void set_up_third_screen(HelloController helloController, int id) {
        helloController.choose_ayat_screen.setVisible(false);
        helloController.show_logo_loading_screen.setVisible(true);
        int start_ayat = return_start_ayat(helloController);
        int end_ayat = return_end_ayat(helloController);
        int surat_number = helloController.choose_the_surat.getSelectionModel().getSelectedIndex() + 1;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (sound_path.isEmpty()) {
                    Reciters_info reciters_info = helloController.list_view_with_the_recitors.getSelectionModel().getSelectedItems().getFirst();
                    sound_mode = Sound_mode.CHOSEN;
                    get_the_sound_and_concat_them_into_one(start_ayat, end_ayat, surat_number, reciters_info);
                } else {
                    sound_mode = Sound_mode.UPLOADED;
                    set_up_sound_for_chosen_verses(start_ayat, end_ayat);
                }
                int start_ayat_section = (int) Math.ceil(start_ayat / 50D);
                int end_ayat_section = (int) Math.ceil(end_ayat / 50D);
                HashMap<String, ArrayList<String>> hashMap_with_all_of_the_translations_of_verses = new HashMap<>();
                for (int i = start_ayat_section; i <= end_ayat_section; i++) {
                    call_verses_api(helloController, id, i, hashMap_with_all_of_the_translations_of_verses);
                }
                set_up_the_languages(helloController, hashMap_with_all_of_the_translations_of_verses);
                if (sound_mode == Sound_mode.CHOSEN) {
                    send_last_screen_analytics_event(id + 1, start_ayat, end_ayat, sound_mode, helloController.list_view_with_the_recitors.getSelectionModel().getSelectedItems().getFirst().getName(), hashMap_with_all_of_the_translations_of_verses.get("arabic"));
                } else {
                    send_last_screen_analytics_event(id + 1, start_ayat, end_ayat, sound_mode, "upload", hashMap_with_all_of_the_translations_of_verses.get("arabic"));
                }
                send_analytics_event("third_screen_opened");
                //update_the_translations(helloController, hashMap_with_all_of_the_translations_of_verses);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        set_up_the_fourth_screen(helloController);
                    }
                });
            }
        });
        executor.shutdown();
    }

    private void add_all_of_the_verses_to_the_list_after(HelloController helloController, String verses_string, HashMap<String, ArrayList<String>> hashMap_with_all_of_the_translations_of_the_verses) {
        JsonNode nameNode = return_name_node(verses_string);
        ArrayNode arrayNode = (ArrayNode) nameNode.get("verses");
        int start_ayat = return_start_ayat(helloController);
        int end_ayat = return_end_ayat(helloController);
        for (int i = 0; i < arrayNode.size(); i++) {
            int ayat_number = Integer.parseInt(String.valueOf(arrayNode.get(i).get("verse_number")));
            if (ayat_number < start_ayat) {
                continue;
            } else if (ayat_number > end_ayat) {
                break;
            }
            {
                String arabic_ayat = String.valueOf(arrayNode.get(i).get("text_uthmani"));
                ArrayList<String> arabic_verses = hashMap_with_all_of_the_translations_of_the_verses.getOrDefault("arabic", new ArrayList<>());
                arabic_verses.add(arabic_ayat);
                hashMap_with_all_of_the_translations_of_the_verses.put("arabic", arabic_verses);
            }
            ArrayNode translations_array_node = (ArrayNode) arrayNode.get(i).get("translations");
            for (JsonNode translation : translations_array_node) {
                int id = translation.get("resource_id").asInt();
                String language_name = hashMap_id_to_language_name_text.get(id);
                if (!language_name.equals("arabic")) {
                    String verse_text = translation.get("text").asText();
                    ArrayList<String> translated_verses = hashMap_with_all_of_the_translations_of_the_verses.getOrDefault(language_name, new ArrayList<>());
                    translated_verses.add(verse_text);
                    hashMap_with_all_of_the_translations_of_the_verses.put(language_name, translated_verses);
                }
            }
            ayats_processed.get(ayat_number - start_ayat).setVerse_number(ayat_number);
        }
    }


    private double return_width_of_the_spinner(ObservableList<String> items, Font font) {
        double max_width = 0;
        for (int i = 0; i < items.size(); i++) {
            Text text = new Text(items.get(i));
            text.setFont(font);
            if (text.getLayoutBounds().getWidth() > max_width) {
                max_width = text.getLayoutBounds().getWidth();
            }
        }
        return max_width;
    }

    private void dalle_spinner_listener(HelloController helloController) {
        helloController.which_chatgpt_to_use.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                set_up_the_quality_spinner(helloController);
            }
        });
    }

    private void ratio_spinner_listen(HelloController helloController) {
        helloController.size_of_image.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (helloController.which_chatgpt_to_use.getValue().equals("Dalle 3")) {
                    if (helloController.size_of_image.getValue().equals("9:16")) {
                        helloController.quality_of_image.getValueFactory().setValue("1024x1792");
                    } else if (helloController.size_of_image.getValue().equals("16:9")) {
                        helloController.quality_of_image.getValueFactory().setValue("1792x1024");
                    } else if (helloController.size_of_image.getValue().equals("1:1")) {
                        helloController.quality_of_image.getValueFactory().setValue("1024x1024");
                    }
                }
            }
        });
    }

    private double set_height_of_prompt_text_area(Font font) {
        Text text = new Text("\n\n\n");
        text.setFont(font);
        return text.getLayoutBounds().getHeight();
    }

    private void reset_all_of_the_advanced_settings(HelloController helloController) {
        helloController.choose_the_surat.scrollTo(0);
        helloController.choose_the_surat.getSelectionModel().clearSelection();
        helloController.which_chatgpt_to_use.getValueFactory().setValue("Dalle 3");
        helloController.size_of_image.getValueFactory().setValue("9:16");
        helloController.style_of_image.getValueFactory().setValue("Natural");
        helloController.quality_of_image.getValueFactory().setValue("1024x1792");
        helloController.generate_chat_gpt_images.setSelected(false);
        helloController.show_advanced_settings_second_screen.setSelected(false);
        helloController.enter_the_prompt.setText("Create a 9:16 image based on this. Do not portray god or any human or add any text.");
        helloController.advanced_setting_v_box.setManaged(false);
        helloController.advanced_setting_v_box.setVisible(false);
        helloController.enter_the_prompt.setManaged(false);
        helloController.enter_the_prompt.setVisible(false);
        helloController.quality_of_image.setManaged(false);
        helloController.quality_of_image.setVisible(false);
        helloController.which_chatgpt_to_use.setManaged(false);
        helloController.which_chatgpt_to_use.setVisible(false);
        helloController.style_of_image.setManaged(false);
        helloController.style_of_image.setVisible(false);
        scroll_to_top_of_combo_box(helloController.combobox_to_choose_starting_ayat);
        scroll_to_top_of_combo_box(helloController.combobox_to_choose_last_ayat);

        helloController.choose_sound_third_screen.setText("Upload Sound");
        helloController.list_view_with_the_recitors.scrollTo(0);
        helloController.list_view_with_the_recitors.getSelectionModel().clearSelection();


//        set_the_play_pause_button(helloController, "play");

        //helloController.sound_slider_fourth_screen.setValue(0);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        selected_verse = 0;
        sound_path = "";
        if (ayats_processed != null) {
            ayats_processed.clear();
        }

        clear_temp_directory();
    }

    // TODO the next previous firing needs to be looked at.

    private void listen_to_previous_button_clicked(HelloController helloController) {
        helloController.previous_page_second_screen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                helloController.choose_surat_screen.setVisible(true);
                helloController.choose_ayat_screen.setVisible(false);
                reset_all_of_the_advanced_settings(helloController);
            }
        });
    }

    private void listen_to_next_button_clicked(HelloController helloController) {
        helloController.next_page_second_screen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                boolean result = is_the_ayat_format_correct(helloController);
                if (!result) {
                    show_alert("The starting verse must be less than or equal to the ending verse.");
                    return;
                }
                if (sound_path.isEmpty() && helloController.list_view_with_the_recitors.getSelectionModel().isEmpty()) {
                    show_alert("Please select a sound before proceeding. You can do so by uploading a sound or by simply selecting a reciter.");
                    return;
                }
                //copy_the_images(helloController, get_the_right_basic_image_aspect_ratio(return_the_aspect_ratio_as_an_object(helloController)));
                set_up_third_screen(helloController, helloController.choose_the_surat.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void watch_the_advanced_settings_box(HelloController helloController) {
        helloController.show_advanced_settings_second_screen.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                helloController.advanced_setting_v_box.setManaged(newValue);
                helloController.advanced_setting_v_box.setVisible(newValue);
                if (!newValue) {
                    reset_all_of_the_advanced_settings(helloController);
                }
            }
        });
    }

    private void set_up_the_which_chat_gpt_to_use(HelloController helloController) {
        SpinnerValueFactory<String> valueFactory =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("Dalle 3", "Dalle 2"));
        valueFactory.setWrapAround(true);
        helloController.which_chatgpt_to_use.setValueFactory(valueFactory);
    }

    private void set_the_width_of_which_gpt_spinner(HelloController helloController) {
        double max_width = return_width_of_the_spinner(((SpinnerValueFactory.ListSpinnerValueFactory<String>) helloController.which_chatgpt_to_use.getValueFactory()).getItems(), helloController.which_chatgpt_to_use.getEditor().getFont());
        helloController.which_chatgpt_to_use.setPrefWidth(max_width + helloController.which_chatgpt_to_use.getEditor().getPadding().getLeft() + helloController.which_chatgpt_to_use.getEditor().getPadding().getRight() + 30);
    }

    private void set_up_the_which_image_spinner(HelloController helloController) {
        SpinnerValueFactory<String> valueFactory =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("9:16", "16:9", "1:1"));
        valueFactory.setWrapAround(true);
        helloController.size_of_image.setValueFactory(valueFactory);
    }

    private void set_the_width_of_image_size(HelloController helloController) {
        double max_width = return_width_of_the_spinner(((SpinnerValueFactory.ListSpinnerValueFactory<String>) helloController.size_of_image.getValueFactory()).getItems(), helloController.size_of_image.getEditor().getFont());
        helloController.size_of_image.setPrefWidth(max_width + helloController.size_of_image.getEditor().getPadding().getLeft() + helloController.size_of_image.getEditor().getPadding().getRight() + 30);
    }

    private void set_up_the_style_spinner(HelloController helloController) {
        SpinnerValueFactory<String> valueFactory =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("Natural", "Vivid"));
        valueFactory.setWrapAround(true);
        helloController.style_of_image.setValueFactory(valueFactory);
    }

    private void set_the_width_of_the_style_spinner(HelloController helloController) {
        double max_width = return_width_of_the_spinner(((SpinnerValueFactory.ListSpinnerValueFactory<String>) helloController.style_of_image.getValueFactory()).getItems(), helloController.style_of_image.getEditor().getFont());
        helloController.style_of_image.setPrefWidth(max_width + helloController.style_of_image.getEditor().getPadding().getLeft() + helloController.style_of_image.getEditor().getPadding().getRight() + 30);
    }

    private void set_up_the_quality_spinner(HelloController helloController) {
        if ((helloController.which_chatgpt_to_use.getValue().equals("Dalle 2"))) {
            SpinnerValueFactory<String> valueFactory =
                    new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("256x256", "512x512", "1024x1024"));
            valueFactory.setWrapAround(true);
            helloController.quality_of_image.setValueFactory(valueFactory);
        } else if ((helloController.which_chatgpt_to_use.getValue().equals("Dalle 3"))) {
            SpinnerValueFactory<String> valueFactory =
                    new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("1024x1792", "1792x1024", "1024x1024"));
            valueFactory.setWrapAround(true);
            helloController.quality_of_image.setValueFactory(valueFactory);
        }
    }

    private void set_the_width_of_the_quality_spinner(HelloController helloController) {
        double max_width = return_width_of_the_spinner(((SpinnerValueFactory.ListSpinnerValueFactory<String>) helloController.quality_of_image.getValueFactory()).getItems(), helloController.quality_of_image.getEditor().getFont());
        helloController.quality_of_image.setPrefWidth(max_width + helloController.quality_of_image.getEditor().getPadding().getLeft() + helloController.quality_of_image.getEditor().getPadding().getRight() + 30);
    }

    private void set_the_height_of_text_prompt_text_area(HelloController helloController) {
        helloController.enter_the_prompt.setPrefHeight(set_height_of_prompt_text_area(helloController.enter_the_prompt.getFont()));
    }

    private void set_up_the_fourth_screen(HelloController helloController) {
        helloController.show_logo_loading_screen.setVisible(false);
        //helloController.show_the_result_screen.setVisible(true);
        //helloController.pane_holding_the_fourth_screen.setVisible(true);
        helloController.show_the_result_screen_stack_pane.setVisible(true);
//        set_the_image_fourth_screen(helloController, 0);
        //set_the_visibility_of_the_buttons(helloController, 0);
        set_selected_verse_text(helloController, 0);
        //set_the_english_text_of_the_ayat_in_the_image_view(helloController, 0);
        set_up_the_media(helloController);
        mediaPlayer_status_changed(helloController);
        set_the_media_player_listener(helloController);
        listen_to_end_of_audio_fourth_screen(helloController);
        set_up_the_width_and_height_of_the_image_in_fourth_screen(helloController);
        animation_timer(helloController);
    }

    /*private void set_the_visibility_of_the_buttons(HelloController helloController, int position) {
        if (position <= 0) {
            helloController.previous_photo_chat_gpt_result.setDisable(true);
        } else {
            helloController.previous_photo_chat_gpt_result.setDisable(false);
        }
        if (position >= ayats_processed.length - 1) {
            helloController.next_photo_chat_gpt_result.setDisable(true);
        } else {
            helloController.next_photo_chat_gpt_result.setDisable(false);
        }
    }*/

    /*private void set_the_image_fourth_screen(HelloController helloController, int position) {

    }*/

    /*private void next_photo_click_listen(HelloController helloController) {
        helloController.next_photo_chat_gpt_result.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selected_verse++;
                the_verse_changed(helloController, selected_verse);
                scroll_to_specific_verse_time(helloController);
                set_the_chatgpt_image_view(helloController, return_the_image_from_time(helloController.time_line_pane, ayats_processed[selected_verse].getStart_millisecond()), Type_of_Image.FULL_QUALITY);
                helloController.list_view_with_all_of_the_languages.refresh();
            }
        });
    }*/

    /*private void previous_photo_click_listen(HelloController helloController) {
        helloController.previous_photo_chat_gpt_result.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selected_verse--;
                the_verse_changed(helloController, selected_verse);
                scroll_to_specific_verse_time(helloController);
                set_the_chatgpt_image_view(helloController, return_the_image_from_time(helloController.time_line_pane, ayats_processed[selected_verse].getStart_millisecond()), Type_of_Image.FULL_QUALITY);
                helloController.list_view_with_all_of_the_languages.refresh();
            }
        });
    }*/

    private void set_selected_verse_text(HelloController helloController, int verse_position) {
        set_the_verse_text_fourth_screen(helloController, ayats_processed.get(verse_position).getVerse_number(), surat_name_selected);
    }

    private void set_the_verse_text_fourth_screen(HelloController helloController, int verse, String surat) {
        String set_me = "Surat ".concat(surat).concat(" verse ").concat(String.valueOf(verse));
        helloController.what_verse_is_this.setText(set_me);
    }

    private void the_verse_changed(HelloController helloController, int selected_verse) {
//        set_the_visibility_of_the_buttons(helloController, selected_verse);
        //add_the_text_to_the_photo(helloController, chatgpt_responses.get(selected_verse).getAyatSettings(), selected_verse);
//        set_the_image_fourth_screen(helloController, selected_verse);
        set_selected_verse_text(helloController, selected_verse);
        loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
        helloController.list_view_with_all_of_the_languages.refresh();
    }

    private void set_up_the_media(HelloController helloController) {
        media = new Media(Paths.get(sound_path).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(1);
    }

    /*private void listen_to_play(HelloController helloController) {
        helloController.play_sound.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                play_or_pause_the_video_after_click(helloController);
            }
        });
    }*/

    private void play_or_pause_the_video_after_click(HelloController helloController) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            pause_the_media_player(helloController);
        } else {
            if (mediaPlayer.getCurrentTime().toMillis() >= get_duration()) {
                mediaPlayer.seek(Duration.ZERO);
            }
            play_the_media_player(helloController);
        }
    }

    private void set_the_media_player_listener(HelloController helloController) {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() { // TODO work is being done here and in AnimationTimer
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                change_the_image_based_on_audio_fourth_screen(helloController, newValue.toMillis() + 10);
                lastKnownMediaTime = TimeUnit.MILLISECONDS.toNanos((long) newValue.toMillis());
                lastKnownSystemTime = System.currentTimeMillis();
            }
        });
    }

    private void media_is_ready(HelloController helloController) {
//        helloController.sound_slider_fourth_screen.setMax(get_duration());
        if (!did_this_play_already) {
            //start_and_unstart_the_media_player(0);
        }
        did_this_play_already = true;
        create_the_time_line(helloController);
    }

    private void listen_to_end_of_audio_fourth_screen(HelloController helloController) {
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                selected_verse = ayats_processed.size() - 1;
                set_the_play_pause_button(helloController, Audio_status.PLAYING);
//                helloController.sound_slider_fourth_screen.setValue(helloController.sound_slider_fourth_screen.getMax());
                timer.stop();
                update_the_time_line_indicator(helloController, get_duration());
                set_the_status_of_locked_in_polygon(helloController, false);
                stop_and_start_the_media_again();
                // TODO stop media after its finished
            }
        });
    }

    private void change_the_image_based_on_audio_fourth_screen(HelloController helloController, Double time_of_audio_millis) {

    }

    private void listen_to_genereate_chat_gpt_checkbox(HelloController helloController) {
        helloController.generate_chat_gpt_images.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                helloController.enter_the_prompt.setManaged(newValue);
                helloController.enter_the_prompt.setVisible(newValue);
                helloController.quality_of_image.setManaged(newValue);
                helloController.quality_of_image.setVisible(newValue);
                helloController.which_chatgpt_to_use.setManaged(newValue);
                helloController.which_chatgpt_to_use.setVisible(newValue);
                helloController.style_of_image.setManaged(newValue);
                helloController.style_of_image.setVisible(newValue);
            }
        });
    }

    /*private void only_allow_digits_and_dash_for_ayat_input(HelloController helloController) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0123456789-]*")) {  // Regex to allow only 1-9, space, and dash
                return change;
            }
            return null; // Reject the change
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        helloController.enter_the_ayats_wanted.setTextFormatter(formatter);
    }*/

    private void set_up_the_width_and_height_of_the_image_in_fourth_screen(HelloController helloController) {
        /*if (helloController.size_of_image.getValue().equals("9:16")) {
            helloController.chatgpt_image_view.setFitWidth(360);
            helloController.chatgpt_image_view.setFitHeight(640);
        } else if (helloController.size_of_image.getValue().equals("16:9")) {
            helloController.chatgpt_image_view.setFitWidth(640);
            helloController.chatgpt_image_view.setFitHeight(360);
        } else if (helloController.size_of_image.getValue().equals("1:1")) {
            helloController.chatgpt_image_view.setFitWidth(360);
            helloController.chatgpt_image_view.setFitHeight(360);
        }*/
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        else return "";
    }

    private void upload_sound_listen(HelloController helloController) {
        helloController.choose_sound_third_screen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilterAudio = new FileChooser.ExtensionFilter("Audio files (*.mp3, *.wav)", "*.mp3", "*.wav");
                fileChooser.getExtensionFilters().add(extFilterAudio);
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    String check_if_mp3 = file.getAbsolutePath().toLowerCase();
                    if (check_if_mp3.endsWith(".mp3")) {
                        audio_frequncy_of_the_sound = get_sample_rate_using_jave(file);
                        sound_path = convert_mp3_to_wav(file, "converted.wav").getAbsolutePath();
                    } else {
                        sound_path = file.getAbsolutePath();
                    }
                    helloController.choose_sound_third_screen.setText("Change Sound");
                    helloController.list_view_with_the_recitors.getSelectionModel().clearSelection();
                }
            }
        });
    }

    private void create_video(HelloController helloController) {
        /*String videoFileName = "/Users/abdelrahmanabdelkader/Downloads/output.mp4";
        File file = new File(videoFileName);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
        int captureWidth = 0;
        int captureHeight = 0;
        if (return_the_aspect_ratio_as_an_object(helloController).equals(Pic_aspect_ratio.aspect_vertical_9_16)) {
            captureWidth = 1080;
            captureHeight = 1920;
        } else if (return_the_aspect_ratio_as_an_object(helloController).equals(Pic_aspect_ratio.aspect_horizontal_16_9)) {
            captureWidth = 1920;
            captureHeight = 1080;
        } else if (return_the_aspect_ratio_as_an_object(helloController).equals(Pic_aspect_ratio.aspect_square_1_1)) {
            captureWidth = 1080;
            captureHeight = 1080;
        }
        int frameRate = 30;

        int audioBitrate = 1024000; // Standard quality

        // Configure the FFmpegFrameRecorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoFileName, captureWidth, captureHeight, number_of_audio_channels);
        recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
        recorder.setAudioCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_AAC);
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setSampleRate(audio_frequncy_of_the_sound);
        recorder.setVideoOption("preset", "veryslow");
        recorder.setVideoOption("crf", "10");
        recorder.setVideoOption("tune", "stillimage");
        recorder.setVideoBitrate(20000000);
        //recorder.setVideoOption("profile", "high");
        recorder.setVideoOption("level", "5.1");
        recorder.setAudioBitrate(audioBitrate);
        recorder.setAudioChannels(number_of_audio_channels);
        recorder.setVideoQuality(0); // Maximum quality
        recorder.setTimestamp(System.currentTimeMillis());
        Java2DFrameConverter converter = new Java2DFrameConverter();
        try {
            recorder.start();
            for (int i = 0; i < chatgpt_responses.size(); i++) {
                BufferedImage image_fake = chatgpt_responses.get(i).getEditied_base_64_image();
                BufferedImage image = new BufferedImage(image_fake.getWidth(), image_fake.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                image.getGraphics().drawImage(image_fake, 0, 0, null);
                long time_on_screen;
                if (i == 0) {
                    time_on_screen = chatgpt_responses.get(i).getTime_in_milliseconds();
                } else {
                    time_on_screen = chatgpt_responses.get(i).getTime_in_milliseconds() - chatgpt_responses.get(i - 1).getTime_in_milliseconds();
                }
                time_on_screen = (time_on_screen * frameRate) / 1000L;
                for (int j = 0; j < time_on_screen; j++) {
                    recorder.record(converter.convert(image));
                }
            }
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sound_path);
            grabber.start();
            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                recorder.record(frame);
            }
            grabber.stop();

            recorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                recorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /*private void add_the_text_to_the_photo(HelloController helloController, Ayat_settings ayatSettings, int selected_verse) {
        BufferedImage bufferedImage = chatgpt_responses.get(selected_verse).getBase_64_image();
        float brightnessFactor = (float) (ayatSettings.getBrightness_of_image() / 100.0);
        RescaleOp rescaleOp = new RescaleOp(brightnessFactor, 0, null);
        rescaleOp.filter(bufferedImage, bufferedImage);
        Graphics2D g = bufferedImage.createGraphics();
        *//*{
            double difference = 100 - ayatSettings.getBrightness_of_image();
            double real_difference = (difference / 100) * 255;
            Color dimColor = new Color(0, 0, 0, (int) real_difference);
            g.setColor(dimColor);
            g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        }*//*
        {
            double font_size = (ayatSettings.getEnglish_font_size() / 500D) * Math.sqrt(Math.pow(bufferedImage.getHeight(), 2) + Math.pow(bufferedImage.getWidth(), 2));
            java.awt.Font font = new java.awt.Font(ayatSettings.getEnglish_font_name(), java.awt.Font.PLAIN, (int) font_size);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            FontMetrics metrics = g.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.decode(ayatSettings.getEnglish_color_hex()));
            ArrayList<String> strings_to_be_drawn;
            if (helloController.enable_english_text.isSelected()) {
                strings_to_be_drawn = wrapText(chatgpt_responses.get(selected_verse).getVerse(), metrics, bufferedImage.getWidth(), 10);
            } else {
                strings_to_be_drawn = new ArrayList<>();
                strings_to_be_drawn.add("");
            }
            double first_y;
            if (ayatSettings.getAlignment().equals("Top")) {
                first_y = metrics.getHeight() * strings_to_be_drawn.size();
            } else if (ayatSettings.getAlignment().equals("Center")) {
                first_y = ((bufferedImage.getHeight() - metrics.getHeight() * strings_to_be_drawn.size()) / 2D) + metrics.getAscent() * strings_to_be_drawn.size();
            } else {
                first_y = bufferedImage.getHeight() + metrics.getAscent() * strings_to_be_drawn.size();
            }
            first_y = first_y + ayatSettings.getEnglish_top_margin() * 0.005D * (double) bufferedImage.getHeight();
            for (int i = strings_to_be_drawn.size() - 1; i >= 0; i--) {
                double x = (bufferedImage.getWidth() - metrics.stringWidth(strings_to_be_drawn.get(i))) / 2D;
                int counter_variable = strings_to_be_drawn.size() - 1 - i;
                double y = first_y - counter_variable * metrics.getHeight();
                g.drawString(strings_to_be_drawn.get(i), (int) x, (int) y);
            }
        }
        {
            double font_size = (ayatSettings.getArabic_font_size() / 500D) * Math.sqrt(Math.pow(bufferedImage.getHeight(), 2) + Math.pow(bufferedImage.getWidth(), 2));
            java.awt.Font font = new java.awt.Font(ayatSettings.getArabic_font_name(), java.awt.Font.PLAIN, (int) font_size);
            FontMetrics metrics = g.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.decode(ayatSettings.getArabic_color_hex()));
            ArrayList<String> strings_to_be_drawn;
            if (helloController.add_arabic_text_fourth_screen.isSelected()) {
                strings_to_be_drawn = wrapText(remove_qoutes_from_arabic_text(chatgpt_responses.get(selected_verse).getArabic_verse()), metrics, bufferedImage.getWidth(), 10);
            } else {
                strings_to_be_drawn = new ArrayList<>();
                strings_to_be_drawn.add("");
            }
            double first_y;
            if (ayatSettings.getArabic_alignment().equals("Top")) {
                first_y = metrics.getHeight() * strings_to_be_drawn.size();
            } else if (ayatSettings.getArabic_alignment().equals("Center")) {
                first_y = ((bufferedImage.getHeight() - metrics.getHeight() * strings_to_be_drawn.size()) / 2D) + metrics.getAscent() * strings_to_be_drawn.size();
            } else {
                first_y = bufferedImage.getHeight() + metrics.getAscent() * strings_to_be_drawn.size();
            }
            first_y = first_y + ayatSettings.getArabic_top_margin() * 0.005D * (double) bufferedImage.getHeight();
            for (int i = strings_to_be_drawn.size() - 1; i >= 0; i--) {
                double x = (bufferedImage.getWidth() - metrics.stringWidth(strings_to_be_drawn.get(i))) / 2D;
                int counter_variable = strings_to_be_drawn.size() - 1 - i;
                double y = first_y - counter_variable * metrics.getHeight();
                g.drawString(strings_to_be_drawn.get(i), (int) x, (int) y);
            }
        }
        {
            double font_size = (ayatSettings.getSurat_font_size() / 500D) * Math.sqrt(Math.pow(bufferedImage.getHeight(), 2) + Math.pow(bufferedImage.getWidth(), 2));
            java.awt.Font font = new java.awt.Font(ayatSettings.getSurat_font_name(), java.awt.Font.PLAIN, (int) font_size);
            FontMetrics metrics = g.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.decode(ayatSettings.getSurat_color_hex()));
            ArrayList<String> strings_to_be_drawn;
            if (helloController.add_surat_name_in_video.isSelected()) {
                strings_to_be_drawn = wrapText(remove_qoutes_from_arabic_text(surat_name_selected), metrics, bufferedImage.getWidth(), 10);
            } else {
                strings_to_be_drawn = new ArrayList<>();
                strings_to_be_drawn.add("");
            }
            double first_y;
            if (ayatSettings.getSurat_alignment().equals("Top")) {
                first_y = metrics.getHeight() * strings_to_be_drawn.size();
            } else if (ayatSettings.getSurat_alignment().equals("Center")) {
                first_y = ((bufferedImage.getHeight() - metrics.getHeight() * strings_to_be_drawn.size()) / 2D) + metrics.getAscent() * strings_to_be_drawn.size();
            } else {
                first_y = bufferedImage.getHeight() + metrics.getAscent() * strings_to_be_drawn.size();
            }
            first_y = first_y + ayatSettings.getSurat_top_margin() * 0.005D * (double) bufferedImage.getHeight();
            for (int i = strings_to_be_drawn.size() - 1; i >= 0; i--) {
                double x = (bufferedImage.getWidth() - metrics.stringWidth(strings_to_be_drawn.get(i))) / 2D;
                int counter_variable = strings_to_be_drawn.size() - 1 - i;
                double y = first_y - counter_variable * metrics.getHeight();
                g.drawString(strings_to_be_drawn.get(i), (int) x, (int) y);
            }
        }
        g.dispose();
        chatgpt_responses.get(selected_verse).setEditied_base_64_image(bufferedImage);
        bufferedImage.flush();
    }*/

    private ArrayList<String> wrapText(String text, FontMetrics metrics, int maxWidth, int margin_left_and_right) {
        double max_width_double = maxWidth;
        max_width_double /= 100;
        max_width_double *= margin_left_and_right;
        String[] text_split = text.split(" ");
        String currentLine = "";
        ArrayList<String> new_strings = new ArrayList<>();
        for (int i = 0; i < text_split.length; i++) {
            int width = metrics.stringWidth(currentLine.concat(text_split[i]).concat(" ")) + (int) max_width_double;
            if (width > maxWidth) {
                new_strings.add(currentLine);
                currentLine = text_split[i].concat(" ");
            } else {
                currentLine = currentLine.concat(text_split[i]).concat(" ");
            }
        }
        new_strings.add(currentLine);
        return new_strings;
    }

    private String update_the_verse_text(String verse_text) {
        verse_text = verse_text.replaceAll("\\d", "");
        verse_text = verse_text.replaceAll("—", "-");
        verse_text = verse_text.replaceAll("˹", "[");
        verse_text = verse_text.replaceAll("˺", "]");
        verse_text = verse_text.replaceAll("\"", "");
        return verse_text;
    }


    private BufferedImage convertToBufferedImage(Image image) {
        if (image == null) {
            return null;
        }

        // Read the Image into a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        if (bufferedImage == null) {
            // In case the Image is not loaded properly, create an empty BufferedImage
            bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
            SwingFXUtils.fromFXImage(image, bufferedImage);
        }
        return bufferedImage;
    }

    private String remove_qoutes_from_arabic_text(String arabic_verse) {
        arabic_verse = arabic_verse.replaceAll("\"", "");
        return arabic_verse;
    }


   /* private void listen_to_cancel_button_third_screen(HelloController helloController) {
        helloController.cancel_video.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //helloController.show_the_result_screen.setVisible(false);
                //helloController.pane_holding_the_fourth_screen.setVisible(false);
                helloController.show_the_result_screen_stack_pane.setVisible(false);
                helloController.choose_surat_screen.setVisible(true);
                reset_all_of_the_advanced_settings(helloController);
                clear_temp_directory();
            }
        });
    }*/

    private String capatilize_first_letter(String text) {
        if (text.isEmpty()) {
            return "";
        }
        if (Character.isLetter(text.charAt(0))) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        } else {
            return text;
        }
    }

    private void get_all_of_the_recitors(HelloController helloController) {
        HttpUrl httpurl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mp3quran.net")
                .addPathSegment("verse")
                .addPathSegment("verse_en.json")
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(httpurl)
                .method("GET", null)
                .addHeader("Accept", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();
            ArrayList<Reciters_info> arrayList_with_reciters_info = process_the_audio_recirotrs(jsonString);
            set_up_the_recitors_list_view(helloController, arrayList_with_reciters_info);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Reciters_info> process_the_audio_recirotrs(String json_text) {
        JsonNode nameNode = return_name_node(json_text);
        ArrayNode arrayNode = (ArrayNode) nameNode.get("reciters_verse");
        HashMap<String, Integer> hashMap_counter = new HashMap<>();
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode local = arrayNode.get(i);
            hashMap_counter.put(local.get("name").asText(), hashMap_counter.getOrDefault(local.get("name").asText(), 0) + 1);
        }
        ArrayList<Reciters_info> reciters_infoArrayList = new ArrayList<>();
        Reciters_info english_reciter = null;
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode local = arrayNode.get(i);
            Reciters_info recitersInfo;
            if (!local.get("audio_url_bit_rate_128").asText().contains("https") && !local.get("audio_url_bit_rate_64").asText().contains("https") && !local.get("audio_url_bit_rate_32_").asText().contains("https")) {
                continue;
            }
            if (hashMap_counter.get(local.get("name").asText()) == 1) {
                recitersInfo = new Reciters_info(local.get("id").asInt(), local.get("name").asText(), local.get("name").asText().replace("Shaik ", ""), local.get("audio_url_bit_rate_32_").asText(), local.get("audio_url_bit_rate_64").asText(), local.get("audio_url_bit_rate_128").asText());
            } else {
                recitersInfo = new Reciters_info(local.get("id").asInt(), local.get("name").asText(), local.get("name").asText().replace("Shaik ", "").concat(" - ").concat(local.get("rewaya").asText()).concat(" - ").concat(local.get("musshaf_type").asText()), local.get("audio_url_bit_rate_32_").asText(), local.get("audio_url_bit_rate_64").asText(), local.get("audio_url_bit_rate_128").asText());
            }
            if (local.get("id").asInt() == 224 && local.get("name").asText().equals("(English) Translated by Sahih International Recited by Ibrahim Walk")) {
                english_reciter = recitersInfo;
            } else {
                reciters_infoArrayList.add(recitersInfo);
            }
        }
        reciters_infoArrayList.sort(new Comparator<Reciters_info>() {
            @Override
            public int compare(Reciters_info o1, Reciters_info o2) {
                return o1.getDisplayed_name().compareTo(o2.getDisplayed_name());
            }
        });
        if (english_reciter != null) {
            reciters_infoArrayList.add(english_reciter);
        }
        return reciters_infoArrayList;
    }

    private void set_up_the_recitors_list_view(HelloController helloController, List<Reciters_info> reciters_info_list) {
        ObservableList<Reciters_info> items = FXCollections.observableArrayList();
        items.addAll(reciters_info_list);
        helloController.list_view_with_the_recitors.setItems(items);
        helloController.list_view_with_the_recitors.setCellFactory(lv -> new ListCell<Reciters_info>() {
            @Override
            protected void updateItem(Reciters_info item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayed_name());  // assuming getName() returns the name property
                    setAlignment(Pos.CENTER); // or Pos.CENTER_LEFT, Pos.CENTER_RIGHT
                }
            }
        });
    }

    private void listen_to_the_recitor_list_view_click(HelloController helloController) {
        helloController.list_view_with_the_recitors.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                sound_path = "";
                helloController.choose_sound_third_screen.setText("Upload Sound");
            }
        });
    }

    private long get_duration() {
        if (media != null) {
            return TimeUnit.MILLISECONDS.toNanos((long) media.getDuration().toMillis());
        } else {
            throw new RuntimeException("Media is null when getting duration");
        }
    }

    private void get_the_sound_and_concat_them_into_one(int start_ayat, int end_ayat, int surat_number_int, Reciters_info recitersInfo) {
        Path base_path = get_os_base_path();
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        client.dispatcher().setMaxRequests(4); // increase concurrency
        client.dispatcher().setMaxRequestsPerHost(2);
        int number_of_ayats = end_ayat - start_ayat + 1;
        int number_of_threads = Math.min(Runtime.getRuntime().availableProcessors(), number_of_ayats);
        BlockingQueue<String> verseQueue = new ArrayBlockingQueue<>(number_of_ayats);
        HashMap<String, Integer> tie_verses_to_indexes = new HashMap<>();
        String base_url = "";
        if (!recitersInfo.getLink_for_128_bits().isEmpty()) {
            base_url = recitersInfo.getLink_for_128_bits();
        } else if (!recitersInfo.getLink_for_64_bits().isEmpty()) {
            base_url = recitersInfo.getLink_for_64_bits();
        } else if (!recitersInfo.getLink_for_32_bits().isEmpty()) {
            base_url = recitersInfo.getLink_for_32_bits();
        }
        //String surat_number = String.format("%03d", helloController.choose_the_surat.getSelectionModel().getSelectedIndex() + 1);
        String surat_number = String.format("%03d", surat_number_int);
        base_url = base_url.concat(surat_number);
        for (int i = start_ayat; i <= end_ayat; i++) {
            String ayat_number = String.format("%03d", i);
            String full_ayat = base_url.concat(ayat_number).concat(".mp3");
            verseQueue.offer(full_ayat);
            tie_verses_to_indexes.put(full_ayat, i);
        }
        for (int i = 0; i < number_of_ayats; i++) {
            ayats_processed.add(new Verse_class_final());
        }
        ExecutorService executor = Executors.newFixedThreadPool(number_of_threads);
        for (int i = 0; i < number_of_threads; i++) {
            int final_i = i;
            executor.submit(() -> {
                while (true) {
                    try {
                        String verse_url = verseQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (verse_url == null) {
                            break; // Exit if no more verses to process
                        }
                        int verse_number = tie_verses_to_indexes.get(verse_url);
                        Request request = new Request.Builder().url(verse_url).build();
                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful()) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        show_alert("There was an error getting the recitation, please try a different recitation or try again later.");
                                    }
                                });
                                System.err.println("code" + response.code());
                                continue;
                            }
                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            try (InputStream input = response.body().byteStream()) {
                                byte[] temp = new byte[16384];
                                int bytesRead;
                                while ((bytesRead = input.read(temp)) != -1) {
                                    buffer.write(temp, 0, bytesRead);
                                }
                            }
                            byte[] mp3Bytes = buffer.toByteArray();
                            File tempFile = new File(base_path.resolve("temp/sound").toString(), String.format("%03d.mp3", verse_number));
                            tempFile.deleteOnExit();
                            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                fos.write(mp3Bytes);
                            }
                            File new_wav_file = convert_mp3_to_wav(tempFile, String.format("%03d.wav", verse_number));
                            new_wav_file.deleteOnExit();
                            ayats_processed.set(verse_number - start_ayat, new Verse_class_final(getDurationWithFFmpeg(new_wav_file)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Thread.sleep(500L * (final_i + 1)); // exponential backoff
                        }
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES); // Wait for all threads to finish
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        File tempFile = new File(base_path.resolve("temp/sound").toString(), String.format("%03d.wav", start_ayat));
        audio_frequncy_of_the_sound = get_sample_rate_using_jave(tempFile);
        int get_the_number_of_audio_channels_local = get_number_of_channels_using_jave(tempFile);
        //int get_the_number_of_audio_channels_local = set_the_number_of_audio_channels(getNumberOfChannels(tempFile.getAbsolutePath()));
        ayats_processed.getFirst().setStart_millisecond(0);
        long start_millisecond = 0;
        if (number_of_ayats > 1) {
            for (int i = 1; i < number_of_ayats; i++) {
                start_millisecond += ayats_processed.get(i - 1).getDuration();
                ayats_processed.get(i).setStart_millisecond(start_millisecond);
            }
        }
        File listFile = new File(base_path.resolve("temp/sound").toString(), "list.txt");
        listFile.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(listFile)) {
            for (int i = start_ayat; i <= end_ayat; i++) {
                String filename = String.format("%03d.mp3", i);
                writer.println("file '" + filename + "'");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
            String ffmpegPath = locator.getExecutablePath();

            // Use ProcessBuilder with the bundled ffmpeg binary
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-f", "concat",
                    "-safe", "0",
                    "-i", listFile.getAbsolutePath(),
                    "-c:a", "pcm_s16le",
                    "-ar", String.valueOf(audio_frequncy_of_the_sound),
                    "-ac", String.valueOf(number_of_audio_channels),
                    base_path.resolve("temp/sound/combined.wav").toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.getInputStream().transferTo(OutputStream.nullOutputStream());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("FFmpeg failed with exit code " + exitCode);
                show_alert("Audio encoding failed. FFMPEG");
            } else {
                File out_put_file = new File(base_path.resolve("temp/sound/combined.wav").toString());
                out_put_file.deleteOnExit();
                sound_path = out_put_file.getAbsolutePath();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void clear_temp_directory() {
        Path base_path = get_os_base_path();
        File base_images_file = new File(base_path.resolve("temp/images/base/").toString());
        File scaled_images_file = new File(base_path.resolve("temp/images/scaled/").toString());
        File sounds_files = new File(base_path.resolve("temp/sound/").toString());
        File converted_images_file = new File(base_path.resolve("temp/converted images/").toString());
        if (base_images_file.exists() && base_images_file.isDirectory()) {
            try {
                FileUtils.cleanDirectory(base_images_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (sounds_files.exists() && sounds_files.isDirectory()) {
            try {
                FileUtils.cleanDirectory(sounds_files);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (scaled_images_file.exists() && scaled_images_file.isDirectory()) {
            try {
                FileUtils.cleanDirectory(scaled_images_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (converted_images_file.exists() && converted_images_file.isDirectory()) {
            try {
                FileUtils.cleanDirectory(converted_images_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private long getDurationWithFFmpeg(File wavFile) {
        try {
            MultimediaObject media = new MultimediaObject(wavFile);
            MultimediaInfo info = media.getInfo();
            long durationMillis = info.getDuration();
            return TimeUnit.MILLISECONDS.toNanos(durationMillis);
        } catch (EncoderException e) {
            System.err.println("Failed to get duration: " + e.getMessage());
        }
        return 0;
    }

    private void make_temp_dir() {
        Path base_path = get_os_base_path();
        File directory = new File(base_path.resolve("temp/").toString());
        File images = new File(base_path.resolve("temp/images").toString());
        File sound = new File(base_path.resolve("temp/sound").toString());
        File images_base = new File(base_path.resolve("temp/images/base").toString());
        File scaled_images_file = new File(base_path.resolve("temp/images/scaled").toString());
        File converted_images_file = new File(base_path.resolve("temp/converted images").toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!images.exists()) {
            images.mkdirs();
        }
        if (!sound.exists()) {
            sound.mkdirs();
        }
        if (!images_base.exists()) {
            images_base.mkdirs();
        }
        if (!scaled_images_file.exists()) {
            scaled_images_file.mkdirs();
        }
        if (!converted_images_file.exists()) {
            converted_images_file.mkdirs();
        }
    }

    private Path get_os_base_path() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Paths.get(appData, app_name);
            }
            return Paths.get(System.getProperty("user.home"), app_name);
        }

        if (os.contains("mac")) {
            return Paths.get(
                    System.getProperty("user.home"),
                    "Library",
                    "Application Support",
                    app_name
            );
        }

        return Paths.get(System.getProperty("user.home"), app_name);
    }

    private BufferedImage return_buffer_image_from_file(String file_path) {
        BufferedImage image = null;
        try {
            File input = new File(file_path);
            if (input.exists()) {
                image = ImageIO.read(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Image buffer_image_to_image(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private BufferedImage image_to_buffered_image(Image image) {
        return SwingFXUtils.fromFXImage(image, null);
    }

    private void scroll_to_specific_verse_time(HelloController helloController) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        long time_in_milliseconds = ayats_processed.get(selected_verse).getStart_millisecond();
        if (!polygon_data.isShould_the_polygon_be_fixed_in_the_middle()) {
            set_the_scroll_pane_h_value_auto_scroll(helloController, return_the_real_x_position_based_on_time(helloController, time_in_milliseconds));
            update_the_time_line_indicator(helloController, time_in_milliseconds);
        }
        mediaPlayer.seek(Duration.millis(TimeUnit.NANOSECONDS.toMillis(time_in_milliseconds)));
        lastKnownSystemTime = 0;
    }

    private String formatTime(double timeInMillis) {
        int totalSeconds = (int) (timeInMillis / 1000);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private Pic_aspect_ratio return_the_aspect_ratio_as_an_object(HelloController helloController) {
        return Pic_aspect_ratio.aspect_vertical_9_16;
    }

    private int getExifOrientation(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MetadataException e) {
            throw new RuntimeException(e);
        }
        return 1; // Default
    }

    private BufferedImage return_the_rotated_image(BufferedImage bufferedImage, int orientation) {
        AffineTransform t = new AffineTransform();
        switch (orientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-bufferedImage.getWidth(), 0);
                break;
            case 3: // PI rotation
                t.translate(bufferedImage.getWidth(), bufferedImage.getHeight());
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -bufferedImage.getHeight());
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(bufferedImage.getHeight(), 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-bufferedImage.getHeight(), 0);
                t.translate(0, bufferedImage.getWidth());
                t.rotate(3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, bufferedImage.getWidth());
                t.rotate(3 * Math.PI / 2);
                break;
        }
        AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage destinationImage = op.createCompatibleDestImage(bufferedImage, (bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY) ? bufferedImage.getColorModel() : null);
        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(bufferedImage, destinationImage);
        return destinationImage;
    }

    private void start_and_unstart_the_media_player(double set_time) {
        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setOnPlaying(null);
                pause_and_listen_to_it(set_time);
            }
        });
        mediaPlayer.setMute(true);
        mediaPlayer.play();
    }

    private void stop_and_start_the_media_again() {
        mediaPlayer.setOnStopped(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setOnStopped(null);
                start_and_unstart_the_media_player(0);
            }
        });
        mediaPlayer.stop();
    }

    private void pause_and_listen_to_it(double set_time) {
        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setOnPaused(null);
                mediaPlayer.setMute(false);
                mediaPlayer.seek(Duration.millis(set_time));
            }
        });
        mediaPlayer.pause();
    }

    private File convert_mp3_to_wav(File input_file, String output_name) {
        Path base_path = get_os_base_path();
        String output_file_path = base_path.resolve("temp/sound/".concat(output_name)).toString();
        int sample_rate = get_sample_rate_using_jave(input_file);
        int number_of_audio_channels_local = get_number_of_channels_using_jave(input_file);
        try {
            /*DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
            String ffmpegPath = locator.getExecutablePath();

            // Use ProcessBuilder with the bundled ffmpeg binary
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-i", input_file.getAbsolutePath(),
                    "-c:a", "pcm_s16le",
                    "-ar", String.valueOf(sample_rate),
                    "-ac", String.valueOf(number_of_audio_channels_local)
                    output_file_path
            );*/
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            audio.setSamplingRate(sample_rate);
            audio.setChannels(number_of_audio_channels_local);

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);

            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(input_file), new File(output_file_path), attrs);
        } catch (Exception exception) {
            System.err.println("Error while converting mp3 file to WAV" + exception.getMessage());
        }
        return new File(output_file_path);
    }

    private int get_sample_rate_using_jave(File audioFile) {
        try {
            MultimediaInfo info = new MultimediaObject(audioFile).getInfo();
            return info.getAudio().getSamplingRate();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            return 0;
        }
    }

    public static int get_number_of_channels_using_jave(File audioFile) {
        MultimediaInfo info = null;
        try {
            info = new MultimediaObject(audioFile).getInfo();
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
        return info.getAudio().getChannels();
    }

    private int set_the_number_of_audio_channels(int get_the_number_of_audio_channels) {
        if (get_the_number_of_audio_channels == -1) {
            number_of_audio_channels = 2;
        } else {
            number_of_audio_channels = get_the_number_of_audio_channels;
        }
        return number_of_audio_channels;
    }

    private boolean is_this_a_mac_device() {
        if (System.getProperty("os.name") == null) {
            return false;
        }
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void convertHeicToJpg(File old_file, File new_file) {
        try {
            String format = "jpg";
            if (new_file.getName().toLowerCase().endsWith("png")) {
                format = "png";
            } else if (new_file.getName().toLowerCase().endsWith("jpg")) {
                format = "jpeg";
            }
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "sips",
                    "-s", "format", format,
                    old_file.getAbsolutePath(),
                    "--out",
                    new_file.getAbsolutePath()
            );
            processBuilder.redirectErrorStream(true); // Merge error and output
            Process process = processBuilder.start();

            // Optional: Read output if you want to debug
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }*/
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                show_alert("HEIC to JPG image conversion failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_up_first_and_last_ayat_combobox(HelloController helloController, int verse_count) {
        ObservableList<Integer> items = FXCollections.observableArrayList();
        for (int i = 1; i <= verse_count; i++) {
            items.add(i);
        }
        helloController.combobox_to_choose_starting_ayat.setItems(items);
        helloController.combobox_to_choose_last_ayat.setItems(items);
    }

    private void initialize_the_combo_box_start_ayat(HelloController helloController) {
        helloController.combobox_to_choose_starting_ayat.getSelectionModel().selectFirst();
        helloController.combobox_to_choose_last_ayat.getSelectionModel().selectFirst();
    }

    private int return_start_ayat(HelloController helloController) {
        return helloController.combobox_to_choose_starting_ayat.getSelectionModel().getSelectedItem();
    }

    private int return_end_ayat(HelloController helloController) {
        return helloController.combobox_to_choose_last_ayat.getSelectionModel().getSelectedItem();
    }

    private boolean is_it_only_one_ayat_selected(HelloController helloController) {
        if (return_start_ayat(helloController) == return_end_ayat(helloController)) {
            return true;
        } else {
            return false;
        }
    }

    private void scroll_to_top_of_combo_box(ComboBox<Integer> comboBox) {
        Skin<?> skin = comboBox.getSkin();
        if (skin instanceof ComboBoxListViewSkin) {
            @SuppressWarnings("unchecked")
            ComboBoxListViewSkin<String> comboBoxListViewSkin = (ComboBoxListViewSkin<String>) skin;
            ListView<String> listView = (ListView<String>) comboBoxListViewSkin.getPopupContent();
            if (listView != null) {
                listView.scrollTo(0);
            }
        }
    }

    private void set_the_icons(HelloController helloController) {
        /*set_pref_min_max(helloController.next_photo_chat_gpt_result, next_prev_button_size * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.next_photo_chat_gpt_result.setShape(new Circle(next_prev_button_size * 2));
        helloController.next_photo_chat_gpt_result.setGraphic(return_the_icon("arrow_forward_ios", next_prev_button_size, next_prev_button_size));
        helloController.next_photo_chat_gpt_result.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.next_photo_chat_gpt_result.setAlignment(Pos.CENTER);

        helloController.previous_photo_chat_gpt_result.setShape(new Circle(next_prev_button_size));
        set_pref_min_max(helloController.previous_photo_chat_gpt_result, next_prev_button_size * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.previous_photo_chat_gpt_result.setGraphic(return_the_icon("arrow_back_ios", next_prev_button_size, next_prev_button_size));
        helloController.previous_photo_chat_gpt_result.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.previous_photo_chat_gpt_result.setAlignment(Pos.CENTER);*/

       /* helloController.full_screen_button_fourth_screen.setGraphic(return_the_icon("fullscreen"), 25D));
        helloController.full_screen_button_fourth_screen.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.full_screen_button_fourth_screen.setAlignment(Pos.CENTER);

        helloController.cancel_video.setGraphic(return_region_for_svg(get_the_svg_path("arrow_back_with_line"), 25D)); TODO add this later
        helloController.cancel_video.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.cancel_video.setAlignment(Pos.CENTER);

        helloController.create_video_final.setGraphic(return_region_for_svg(get_the_svg_path("rocket_launch"), 25D)); TODO add this later
        helloController.create_video_final.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.create_video_final.setAlignment(Pos.CENTER);*/

        /*helloController.play_sound.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.play_sound.setAlignment(Pos.CENTER);*/

        Shape squirqle = return_default_squircle();
        //set_the_graphic_for_double_rewind_button
        set_pref_min_max(helloController.fast_rewind_button, width_and_height_of_the_control_buttons * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.fast_rewind_button.setShape(squirqle);
        helloController.fast_rewind_button.setGraphic(return_the_icon("fast_rewind", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
        helloController.fast_rewind_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.fast_rewind_button.setAlignment(Pos.CENTER);

        //set_the_graphic_for_double_rewind_button
        set_pref_min_max(helloController.rewind_button, width_and_height_of_the_control_buttons * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.rewind_button.setShape(squirqle);
        helloController.rewind_button.setGraphic(return_the_icon("rewind", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
        helloController.rewind_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.rewind_button.setAlignment(Pos.CENTER);

        //set_the_graphic_for_double_rewind_button
        set_pref_min_max(helloController.play_pause_button, width_and_height_of_the_control_buttons * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.play_pause_button.setShape(squirqle);
        helloController.play_pause_button.setGraphic(return_the_icon("play_arrow", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
        helloController.play_pause_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.play_pause_button.setAlignment(Pos.CENTER);

        //set_the_graphic_for_double_rewind_button
        set_pref_min_max(helloController.forward_button, width_and_height_of_the_control_buttons * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.forward_button.setShape(squirqle);
        helloController.forward_button.setGraphic(return_the_icon("forward", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
        helloController.forward_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.forward_button.setAlignment(Pos.CENTER);

        //set_the_graphic_for_double_rewind_button
        set_pref_min_max(helloController.fast_forward_button, width_and_height_of_the_control_buttons * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.fast_forward_button.setShape(squirqle);
        helloController.fast_forward_button.setGraphic(return_the_icon("fast_forward", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
        helloController.fast_forward_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.fast_forward_button.setAlignment(Pos.CENTER);
    }

    private void set_pref_min_max(Region region, double value, Resize_bind_type resizeType) {
        if (resizeType == Resize_bind_type.WIDTH_AND_HEIGHT || resizeType == Resize_bind_type.HEIGHT) {
            region.setPrefHeight(value);
            region.setMinHeight(value);
            region.setMaxHeight(value);
        }

        if (resizeType == Resize_bind_type.WIDTH_AND_HEIGHT || resizeType == Resize_bind_type.WIDTH) {
            region.setPrefWidth(value);
            region.setMinWidth(value);
            region.setMaxWidth(value);
        }
    }

    /*private String getSvgPathContent(String resourcePath) {
        String final_resource_path = "/icons/".concat(resourcePath).concat(".svg");
        try (InputStream inputStream = getClass().getResourceAsStream(final_resource_path)) {
            if (inputStream == null) {
                System.err.println("Could not find SVG file: " + resourcePath);
                return null;
            }
            String svgData = new String(inputStream.readAllBytes());

            // Regex to find the d="..." inside <path>
            Pattern pattern = Pattern.compile("d\\s*=\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(svgData);

            if (matcher.find()) {
                return matcher.group(1); // The actual path data for SVGPath
            } else {
                System.err.println("No <path> with d attribute found in SVG.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /*private Region return_region_for_svg(SVGPath svgPath, double max_width_height) {
        double[] width_and_height = return_real_width_and_height_of_svg(svgPath, max_width_height);
        Region region = new Region();
        region.setShape(svgPath);
        region.setMinSize(width_and_height[0], width_and_height[1]);
        region.setPrefSize(width_and_height[0], width_and_height[1]);
        region.setMaxSize(width_and_height[0], width_and_height[1]);
        region.setStyle("-fx-background-color: black;");
        return region;
    }*/

    private double[] return_real_width_and_height_of_svg(SVGPath svgPath, double max_width_height) {
        Bounds bounds = svgPath.getBoundsInLocal();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (width > height) {
            return new double[]{max_width_height, max_width_height * (height / width)};
        } else if (width == height) {
            return new double[]{max_width_height, max_width_height};
        } else {
            return new double[]{max_width_height * (width / height), max_width_height};
        }
    }

    /*private SVGPath get_the_svg_path(String file_name) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(getSvgPathContent(file_name)); // <-- only the path string
        svgPath.setFill(javafx.scene.paint.Paint.valueOf("#000000"));
        return svgPath;
    }*/

    private void listen_to_enter_click_on_select_surat_listview(HelloController helloController) {
        helloController.choose_the_surat.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                helloController.choose_surat_screen.setVisible(false);
                helloController.choose_ayat_screen.setVisible(true);
                set_up_second_screen(helloController, helloController.choose_the_surat.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void listen_to_upload_media_button(HelloController helloController) {
        helloController.add_media_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                upload_media_has_been_clicked(helloController);
                send_analytics_event("Media uploaded");
            }
        });
    }

    private void upload_media_has_been_clicked(HelloController helloController) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter image_filter;
        if (is_this_a_mac_device()) {
            image_filter = new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg, *.heic)", "*.png", "*.PNG", "*.jpg", "*.JPG", "*.jpeg", "*.JPEG", "*.heic", "*.HEIC");
        } else {
            image_filter = new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.PNG", "*.jpg", "*.JPG", "*.jpeg", "*.JPEG");
        }
        fileChooser.getExtensionFilters().addAll(image_filter);
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        add_the_images_to_the_media_pool_in_the_back_ground(helloController, files);
    }

    private void add_the_images_to_the_media_pool_in_the_back_ground(HelloController helloController, List<File> files) {
        Path base_path = get_os_base_path();
        if (files != null && !files.isEmpty()) {
            helloController.progress_indicator_media_pool.setVisible(true);
            helloController.scroll_pane_hosting_tile_pane_media_pool.setDisable(true);
            helloController.upload_media_text.setDisable(true);
            List<Media_pool> arrayList_with_media_pool = new ArrayList<>();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    for (int i = 0; i < files.size(); i++) {
                        File image_file = files.get(i);
                        boolean did_the_image_get_down_scaled = false;
                        String fileName_lower_case = image_file.getName().toLowerCase();
                        Image image;
                        if (fileName_lower_case.endsWith("heic")) {
                            File new_jpg_file = new File(base_path.resolve("temp/converted images/".concat(UUID.randomUUID().toString()).concat(".jpg")).toString());
                            new_jpg_file.deleteOnExit();
                            convertHeicToJpg(image_file, new_jpg_file);
                            image = new Image(new FileInputStream(new_jpg_file));
                        } else if (fileName_lower_case.endsWith("png")) {
                            File new_jpg_file = new File(base_path.resolve("temp/converted images/".concat(UUID.randomUUID().toString()).concat(".jpg")).toString());
                            new_jpg_file.deleteOnExit();
                            convert_png_to_jpg(image_file, new_jpg_file);
                            image = new Image(new FileInputStream(new_jpg_file));
                        } else {
                            image = new Image(new FileInputStream(image_file));
                        }
                        String image_id = UUID.randomUUID().toString();
                        BufferedImage bufferedImage = image_to_buffered_image(image);
                        int orientation = getExifOrientation(image_file);
                        if (orientation == 3 || orientation == 6 || orientation == 8) {
                            bufferedImage = return_the_rotated_image(bufferedImage, orientation);
                        }
                        Pic_aspect_ratio picAspectRatio = return_the_aspect_ratio_as_an_object(helloController);
                        Media_pool mediaPool_item;
                        bufferedImage = return_argb_buffered_image(bufferedImage);
                        if (is_this_the_correct_ratio(picAspectRatio, bufferedImage)) {
                            write_the_raw_file(bufferedImage, base_path.resolve("temp/images/base").toString(), image_id);
                            if (do_i_need_to_down_scale(bufferedImage, picAspectRatio)) {
                                did_the_image_get_down_scaled = true;
                                BufferedImage down_sized_buffed_image = return_argb_buffered_image(return_resized_downscale_buffer_image(bufferedImage, picAspectRatio));
                                write_the_raw_file(down_sized_buffed_image, base_path.resolve("temp/images/scaled").toString(), image_id);
                            }
                            mediaPool_item = new Media_pool(image_id, create_a_thumbnail(bufferedImage, picAspectRatio), image_file.getName(), did_the_image_get_down_scaled, bufferedImage.getWidth(), bufferedImage.getHeight());
                        } else {
                            BufferedImage filled_with_black = fill_the_back_ground_with_color(bufferedImage, new Color(0, 0, 0, 255));
                            BufferedImage filled_transparent = fill_the_back_ground_with_color(bufferedImage, new Color(0, 0, 0, 0));
                            write_the_raw_file(filled_with_black, base_path.resolve("temp/images/base").toString(), image_id);
                            if (do_i_need_to_down_scale(filled_with_black, picAspectRatio)) {
                                did_the_image_get_down_scaled = true;
                                BufferedImage down_sized_buffed_image = return_argb_buffered_image(return_resized_downscale_buffer_image(filled_with_black, picAspectRatio));
                                write_the_raw_file(down_sized_buffed_image, base_path.resolve("temp/images/scaled").toString(), image_id);
                            }
                            mediaPool_item = new Media_pool(image_id, create_a_thumbnail(filled_transparent, picAspectRatio), create_a_thumbnail(filled_with_black, picAspectRatio), image_file.getName(), did_the_image_get_down_scaled, filled_with_black.getWidth(), filled_with_black.getHeight());
                        }
                        arrayList_with_media_pool.add(mediaPool_item);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < arrayList_with_media_pool.size(); i++) {
                                add_image_to_tile_pane(helloController, arrayList_with_media_pool.get(i));
                                hashMap_with_media_pool_items.put(arrayList_with_media_pool.get(i).getId(), arrayList_with_media_pool.get(i));
                            }
                            helloController.upload_media_text.setDisable(false);
                            helloController.progress_indicator_media_pool.setVisible(false);
                            helloController.scroll_pane_hosting_tile_pane_media_pool.setDisable(false);
                            hide_or_show_media_pool(helloController);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.shutdown();
        }
    }

    private void write_the_raw_file(BufferedImage bufferedImage, String file_path, String name) {
        int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        File file = new File(file_path.concat("/").concat(name).concat(".raw"));
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 3);
            byteBuffer.order(ByteOrder.BIG_ENDIAN); // or LITTLE_ENDIAN depending on your platform
            for (int pixel : pixels) {
                //int alpha = (pixel >> 24) & 0xFF;
                byte red = (byte) (pixel >> 16);
                byte green = (byte) (pixel >> 8);
                byte blue = (byte) pixel;
                byteBuffer.put(red);
                byteBuffer.put(green);
                byteBuffer.put(blue);
            }
            fos.write(byteBuffer.array());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean do_i_need_to_down_scale(BufferedImage bufferedImage, Pic_aspect_ratio pic_aspect_ratio) {
        if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_vertical_9_16)) {
            if (bufferedImage.getWidth() > 1080 || bufferedImage.getHeight() > 1920) {
                return true;
            }
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_horizontal_16_9)) {
            if (bufferedImage.getWidth() > 1920 || bufferedImage.getHeight() > 1080) {
                return true;
            }
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_square_1_1)) {
            if (bufferedImage.getWidth() > 1080 || bufferedImage.getHeight() > 1080) {
                return true;
            }
        }
        return false;
    }

    private BufferedImage return_resized_downscale_buffer_image(BufferedImage bufferedImage, Pic_aspect_ratio pic_aspect_ratio) {
        int[] height_and_width = return_width_and_height_based_on_ratio(pic_aspect_ratio);
        int width = height_and_width[0];
        int height = height_and_width[1];
        if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_vertical_9_16)) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, width, height);
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_horizontal_16_9)) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, width, height);
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_square_1_1)) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, width, height);
        }
        return bufferedImage;
    }

    private Image create_a_thumbnail(BufferedImage bufferedImage, Pic_aspect_ratio pic_aspect_ratio) {
        int[] width_and_height = return_width_and_height_based_on_ratio(pic_aspect_ratio);
        int width = width_and_height[0] / 4;
        int height = width_and_height[1] / 4;
        try {
            BufferedImage thumbnail = Thumbnails.of(bufferedImage)
                    .size(width, height)
                    .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                    .asBufferedImage();
            return buffer_image_to_image(thumbnail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void add_image_to_tile_pane(HelloController helloController, Media_pool mediaPool) {
        StackPane stackPane = new StackPane();
        //stackPane.setStyle("-fx-cursor: OPEN_HAND;");
        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView(mediaPool.getThumbnail());
        imageView.setFitHeight(image_view_in_tile_pane_height);
        imageView.setFitWidth(image_view_in_tile_pane_width);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCursor(Cursor.OPEN_HAND);
        Label label = new Label(mediaPool.getOriginal_image_name());
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(imageView.getFitWidth());
        label.setWrapText(false);
        label.setTextOverrun(OverrunStyle.ELLIPSIS);
        vBox.getChildren().setAll(imageView, label);
        stackPane.getChildren().add(vBox);
        stackPane.setUserData(mediaPool);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Remove media");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                helloController.tile_pane_media_pool.getChildren().remove(stackPane);
                hide_or_show_media_pool(helloController);
            }
        });
        contextMenu.getItems().add(item1);
        stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(stackPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                if (empty_tile_pane_context_menu != null) {
                    empty_tile_pane_context_menu.hide();
                }
            }
        });
        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                    Rectangle created_rectangle = create_and_return_time_line_rectangle(helloController.time_line_pane, time_line_pane_data.getTime_line_base_line(), nanoseconds_to_pixels(time_line_pane_data, TimeUnit.SECONDS.toNanos(1)));
                    created_rectangle.setVisible(false);
                    Shape_object_time_line shapeObjectTimeLine = new Shape_object_time_line(0, nanoseconds_to_pixels(time_line_pane_data, TimeUnit.SECONDS.toNanos(1)), created_rectangle, mediaPool.getId(), 0, TimeUnit.SECONDS.toNanos(1), new Opacity_settings());
                    set_up_the_image_rectangle(created_rectangle, mediaPool.getThumbnail(), helloController.time_line_pane, new Opacity_settings());
                    configure_the_image_rectangle(shapeObjectTimeLine, helloController, helloController.time_line_pane, mediaPool.getThumbnail());
                    imageView.setCursor(Cursor.CLOSED_HAND);
                    double real_x_pos = mouseEvent.getSceneX() - mouseEvent.getX();
                    double real_y_pos = mouseEvent.getSceneY() - mouseEvent.getY();
                    ImageView ghost = new ImageView(imageView.getImage());
                    ghost.setFitWidth(image_view_in_tile_pane_width);
                    ghost.setFitHeight(image_view_in_tile_pane_height);
                    ghost.setOpacity(0.75);
                    ghost.setLayoutX(real_x_pos);
                    ghost.setLayoutY(real_y_pos);
                    Media_pool_item_dragged media_pool_item_dragged = new Media_pool_item_dragged(ghost, mouseEvent.getSceneX(), mouseEvent.getSceneY(), mediaPool.getId(), shapeObjectTimeLine, time_line_pane_data.getTree_set_containing_all_of_the_items());
                    helloController.pane_holding_the_fourth_screen.getChildren().add(ghost);
                    imageView.setUserData(media_pool_item_dragged);
                    send_analytics_event("Media_pool_image_clicked");
                }
            }
        });
        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Media_pool_item_dragged media_pool_item_dragged = (Media_pool_item_dragged) imageView.getUserData();
                    if (media_pool_item_dragged != null) {
                        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                        ImageView ghost = media_pool_item_dragged.getImageView();
                        double old_x_pos = media_pool_item_dragged.getX_pos();
                        double old_y_pos = media_pool_item_dragged.getY_pos();
                        double x_pos_local = helloController.time_line_pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
                        double image_width_in_time_line = nanoseconds_to_pixels(time_line_pane_data, TimeUnit.SECONDS.toNanos(1));
                        Shape_object_time_line shapeObjectTimeLine = media_pool_item_dragged.getShapeObjectTimeLine();
                        if (!helloController.pane_holding_the_fourth_screen.getChildren().contains(ghost)) {
                            helloController.pane_holding_the_fourth_screen.getChildren().add(ghost);
                        }
                        ghost.setTranslateX(mouseEvent.getSceneX() - old_x_pos);
                        ghost.setTranslateY(mouseEvent.getSceneY() - old_y_pos);
                        if (media_pool_item_dragged.isHas_this_been_dragged() || am_i_in_time_line_boundries(helloController, mouseEvent.getSceneX(), mouseEvent.getSceneY(), media_pool_item_dragged)) {
                            if (!shapeObjectTimeLine.getShape().isVisible()) {
                                shapeObjectTimeLine.getShape().setVisible(true);
                            }
                            double start = move_the_rectangle(helloController.time_line_pane, (Rectangle) shapeObjectTimeLine.getShape(), x_pos_local);
                            shapeObjectTimeLine.setStart(start);
                            shapeObjectTimeLine.setStart_time(pixels_to_nanoseconds(time_line_pane_data, start - time_line_pane_data.getTime_line_base_line()));
                            shapeObjectTimeLine.setEnd(start + ((Rectangle) shapeObjectTimeLine.getShape()).getWidth());
                            shapeObjectTimeLine.setEnd_time(pixels_to_nanoseconds(time_line_pane_data, start + ((Rectangle) shapeObjectTimeLine.getShape()).getWidth() - time_line_pane_data.getTime_line_base_line()));
                        }
                        double min_x_pos_local;
                        double max_x_pos_local;
                        if (x_pos_local - image_width_in_time_line / 2 >= time_line_pane_data.getTime_line_base_line() && x_pos_local + image_width_in_time_line / 2 <= time_line_pane_data.getTime_line_end_base_line()) {
                            min_x_pos_local = x_pos_local - image_width_in_time_line / 2;
                            max_x_pos_local = x_pos_local + image_width_in_time_line / 2;
                        } else {
                            if (x_pos_local - image_width_in_time_line / 2 < time_line_pane_data.getTime_line_base_line()) {
                                min_x_pos_local = time_line_pane_data.getTime_line_base_line();
                                max_x_pos_local = time_line_pane_data.getTime_line_base_line() + image_width_in_time_line;
                            } else {
                                max_x_pos_local = time_line_pane_data.getTime_line_end_base_line();
                                min_x_pos_local = time_line_pane_data.getTime_line_end_base_line() - image_width_in_time_line;
                            }
                        }
                        boolean should_i_be_visible = am_i_in_time_line_boundries(helloController, mouseEvent.getSceneX(), mouseEvent.getSceneY(), media_pool_item_dragged) && !is_there_is_a_collosion(time_line_pane_data.getTree_set_containing_all_of_the_items(), min_x_pos_local, max_x_pos_local);
                        if (should_i_be_visible) {
                            set_the_opacity_of_the_rectangle_in_time_line_pane((Rectangle) shapeObjectTimeLine.getShape(), 1);
                            double polygon_x_pos = return_polygon_middle_position(time_line_pane_data);
                            if (polygon_x_pos >= min_x_pos_local && polygon_x_pos <= max_x_pos_local) {
                                set_the_chatgpt_image_view(helloController, media_pool_item_dragged.getImage_key_uuid(), Type_of_Image.THUMBNAIL_QUALITY);
                                media_pool_item_dragged.setDid_this_change_the_image(true);
                                create_and_attach_all_the_image_listeners(helloController, shapeObjectTimeLine);
                                enable_the_image_control_panel(helloController, shapeObjectTimeLine);
                            } else if (media_pool_item_dragged.isDid_this_change_the_image()) {
                                set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
                                if (media_pool_item_dragged.isDid_this_change_the_image()) {
                                    detach_all_the_image_control_listeners(helloController, shapeObjectTimeLine);
                                    disable_the_image_control_panel(helloController);
                                }
                            }
                        } else {
                            set_the_opacity_of_the_rectangle_in_time_line_pane((Rectangle) shapeObjectTimeLine.getShape(), 0.4D);
                            if (media_pool_item_dragged.isDid_this_change_the_image()) {
                                set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
                                detach_all_the_image_control_listeners(helloController, shapeObjectTimeLine);
                                disable_the_image_control_panel(helloController);
                            }
                        }
                    }
                }
            }
        });
        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    imageView.setCursor(Cursor.OPEN_HAND);
                    Media_pool_item_dragged media_pool_item_dragged = (Media_pool_item_dragged) imageView.getUserData();
                    if (media_pool_item_dragged != null) {
                        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                        ImageView ghost = media_pool_item_dragged.getImageView();
                        helloController.pane_holding_the_fourth_screen.getChildren().remove(ghost);
                        double x_pos_local = helloController.time_line_pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
                        double image_width_in_time_line = nanoseconds_to_pixels(time_line_pane_data, TimeUnit.SECONDS.toNanos(1));
                        double min_x_pos_local;
                        double max_x_pos_local;
                        if (x_pos_local - image_width_in_time_line / 2 >= time_line_pane_data.getTime_line_base_line() && x_pos_local + image_width_in_time_line / 2 <= time_line_pane_data.getTime_line_end_base_line()) {
                            min_x_pos_local = x_pos_local - image_width_in_time_line / 2;
                            max_x_pos_local = x_pos_local + image_width_in_time_line / 2;
                        } else {
                            if (x_pos_local - image_width_in_time_line / 2 < time_line_pane_data.getTime_line_base_line()) {
                                min_x_pos_local = time_line_pane_data.getTime_line_base_line();
                                max_x_pos_local = time_line_pane_data.getTime_line_base_line() + image_width_in_time_line;
                            } else {
                                max_x_pos_local = time_line_pane_data.getTime_line_end_base_line();
                                min_x_pos_local = time_line_pane_data.getTime_line_end_base_line() - image_width_in_time_line;
                            }
                        }
                        double polygon_x_pos = return_polygon_middle_position(time_line_pane_data);
                        if (am_i_in_time_line_boundries(helloController, mouseEvent.getSceneX(), mouseEvent.getSceneY(), media_pool_item_dragged) && !is_there_is_a_collosion(media_pool_item_dragged.getTree_set_containing_all_of_the_items(), min_x_pos_local, max_x_pos_local)) {
                            time_line_pane_data.getTree_set_containing_all_of_the_items().add(media_pool_item_dragged.getShapeObjectTimeLine());
                            if (polygon_x_pos >= min_x_pos_local && polygon_x_pos <= max_x_pos_local) {
                                set_the_chatgpt_image_view(helloController, media_pool_item_dragged.getImage_key_uuid(), Type_of_Image.FULL_QUALITY);
                            } else {
                                set_the_chatgpt_image_view(helloController, return_the_image_on_click(helloController.time_line_pane, polygon_x_pos), Type_of_Image.FULL_QUALITY);
                                if (media_pool_item_dragged.isDid_this_change_the_image()) {
                                    // TODO look at this
                                }
                            }
                        } else {
                            helloController.time_line_pane.getChildren().remove(media_pool_item_dragged.getShapeObjectTimeLine().getShape());
                            if (media_pool_item_dragged.isDid_this_change_the_image()) {
                                set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
                            }
                        }
                    }
                }
            }
        });
        helloController.tile_pane_media_pool.getChildren().add(stackPane);
    }

    private void set_the_width_of_the_left_and_right(HelloController helloController) {
        helloController.center_group_grid_pane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> obs, Bounds oldVal, Bounds newVal) {
                double width_of_the_screen = Screen.getPrimary().getBounds().getWidth();
                double width_of_center = newVal.getWidth();
                double width_of_left_and_right = (width_of_the_screen - width_of_center) / 2;
                helloController.left_stack_pane_in_grid_pane.setPrefWidth(width_of_left_and_right);
                helloController.left_stack_pane_in_grid_pane.setMaxWidth(width_of_left_and_right);
                helloController.right_stack_pane_in_grid_pane.setPrefWidth(width_of_left_and_right);
                helloController.right_stack_pane_in_grid_pane.setMaxWidth(width_of_left_and_right);
                helloController.center_group_grid_pane.layoutBoundsProperty().removeListener(this);
            }
        });
    }

    private void listen_to_top_and_bottom_pane_size_change_fourth_screen(HelloController helloController, Scene scene) {
        double screen_height;
        if (scene.getHeight() > 0) {
            screen_height = scene.getHeight();
        } else {
            screen_height = Screen.getPrimary().getBounds().getHeight() * screen_height_multiplier;
        }
        /*if (helloController.top_pane_fourth_screen.getHeight() > 0) {
            max_hight_of_top_pane_fourth_screen = Math.max(helloController.top_pane_fourth_screen.getHeight(), max_hight_of_top_pane_fourth_screen);
            reseize_the_image_view(helloController, screen_height);
        }*/
        if (helloController.bottom_stack_pane_fourth_screen.getHeight() > 0) {
            max_hight_of_bottom_pane_fourth_screen = Math.max(max_hight_of_bottom_pane_fourth_screen, helloController.bottom_stack_pane_fourth_screen.getHeight());
            reseize_the_image_view(helloController, screen_height);
        }
        /*helloController.top_pane_fourth_screen.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds old_bounds, Bounds new_bounds) {
                max_hight_of_top_pane_fourth_screen = Math.max(max_hight_of_top_pane_fourth_screen, new_bounds.getHeight());
                if (!helloController.show_the_result_screen_stack_pane.isVisible()) {
                    if (scene.getHeight() == 0) {
                        reseize_the_image_view(helloController, Screen.getPrimary().getBounds().getHeight() / 2);
                    } else {
                        reseize_the_image_view(helloController, scene.getHeight());
                    }
                }
            }
        });*/
        helloController.bottom_stack_pane_fourth_screen.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds old_bounds, Bounds new_bounds) {
                max_hight_of_bottom_pane_fourth_screen = Math.max(max_hight_of_bottom_pane_fourth_screen, new_bounds.getHeight());
                if (!helloController.show_the_result_screen_stack_pane.isVisible()) {
                    if (scene.getHeight() == 0) {
                        reseize_the_image_view(helloController, Screen.getPrimary().getBounds().getHeight() / 2);
                    } else {
                        reseize_the_image_view(helloController, scene.getHeight());
                    }
                }
            }
        });
    }

    private void listen_to_whole_screen_resize(Scene scene, HelloController helloController) {
        if (scene.getHeight() > 0) {
            reseize_the_image_view(helloController, scene.getHeight());
        }
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                reseize_the_image_view(helloController, new_number.doubleValue());
            }
        });
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                reseize_the_image_view(helloController, scene.getHeight());
            }
        });
    }

    private void reseize_the_image_view(HelloController helloController, double main_stage_height) {
        double height_of_the_screen = main_stage_height - (helloController.what_verse_is_this.getHeight() + 15 + max_hight_of_top_pane_fourth_screen + max_hight_of_bottom_pane_fourth_screen);
        double width_of_chat_gpt_image_view = (height_of_the_screen / 16) * 9;
        if (height_of_the_screen < 1 || width_of_chat_gpt_image_view < 1) {
            return;
        }
        helloController.chatgpt_image_view.setFitHeight(height_of_the_screen);
        helloController.chatgpt_image_view.setFitWidth(width_of_chat_gpt_image_view);
        helloController.stack_pane_of_image_view_and_text.setPrefHeight(height_of_the_screen);
        helloController.stack_pane_of_image_view_and_text.setMinHeight(height_of_the_screen);
        helloController.stack_pane_of_image_view_and_text.setMaxHeight(height_of_the_screen);
        helloController.stack_pane_of_image_view_and_text.setPrefWidth(width_of_chat_gpt_image_view);
        helloController.stack_pane_of_image_view_and_text.setMinWidth(width_of_chat_gpt_image_view);
        helloController.stack_pane_of_image_view_and_text.setMaxWidth(width_of_chat_gpt_image_view);
    }

    private void hide_or_show_media_pool(HelloController helloController) {
        if (helloController.tile_pane_media_pool.getChildren().isEmpty()) {
            helloController.upload_media_text.setManaged(true);
            helloController.upload_media_text.setVisible(true);
        } else {
            helloController.upload_media_text.setManaged(false);
            helloController.upload_media_text.setVisible(false);
        }
    }

    private void set_the_play_pause_button(HelloController helloController, Audio_status audioStatus) {
        if (audioStatus == Audio_status.PLAYING) {
            helloController.play_pause_button.setGraphic(return_the_icon("play_arrow", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
//            helloController.play_sound.getProperties().put("type", "play");
        } else if (audioStatus == Audio_status.PAUSED) {
            Region region = new Region();
            helloController.play_pause_button.setGraphic(return_the_icon("pause", (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control), (int) (width_and_height_of_the_control_buttons * multiplier_for_the_icons_inside_the_buttons_audio_control)));
//            helloController.play_sound.getProperties().put("type", "pause");
        }
    }

    private int[] return_width_and_height_based_on_ratio(Pic_aspect_ratio pic_aspect_ratio) {
        int[] height_and_width = new int[2];
        if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_vertical_9_16)) {
            height_and_width[0] = 1080;
            height_and_width[1] = 1920;
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_horizontal_16_9)) {
            height_and_width[0] = 1920;
            height_and_width[1] = 1080;
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_square_1_1)) {
            height_and_width[0] = 1080;
            height_and_width[1] = 1080;
        }
        if (height_and_width[0] == 0 && height_and_width[1] == 0) {
            return new int[]{1080, 1920};
        } else {
            return height_and_width;
        }
    }

    private void listen_to_tile_pane_size_change(HelloController helloController) {
        helloController.tile_pane_media_pool.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds old_bounds, Bounds new_bounds) {
                double width = new_bounds.getWidth();
                int number_of_items = (int) width / image_view_in_tile_pane_width;
                if (number_of_items == 0 || number_of_items == 1) {
                    return;
                }
                double space_left = Math.floor(width % image_view_in_tile_pane_width);
                helloController.tile_pane_media_pool.setPrefColumns(number_of_items);
                helloController.tile_pane_media_pool.setHgap(Math.floor(space_left / (number_of_items - 1)));
            }
        });
    }

    private boolean is_this_the_correct_ratio(Pic_aspect_ratio pic_aspect_ratio, BufferedImage bufferedImage) {
        if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_vertical_9_16)) {
            if (bufferedImage.getWidth() * 16D == bufferedImage.getHeight() * 9D) {
                return true;
            } else {
                return false;
            }
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_horizontal_16_9)) {
            if (bufferedImage.getWidth() * 9D == bufferedImage.getHeight() * 16D) {
                return true;
            } else {
                return false;
            }
        } else if (pic_aspect_ratio.equals(Pic_aspect_ratio.aspect_square_1_1)) {
            if (bufferedImage.getHeight() == bufferedImage.getWidth()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /*private BufferedImage set_the_background_of_the_image_to_black(BufferedImage bufferedImage) {
        double max_width = bufferedImage.getWidth() * 16D;
        double max_height = bufferedImage.getHeight() * 9D;
        double new_image_height;
        double new_image_width;
        if (max_width > max_height) {
            new_image_width = bufferedImage.getWidth();
            new_image_height = (bufferedImage.getWidth() / 9D) * 16D;
            int height_difference = (int) (new_image_height - bufferedImage.getHeight());
            int add_one = 0;
            if (height_difference % 2 == 1) {
                add_one = 1;
            }
            WritableImage writable_image = new WritableImage((int) new_image_width, (int) new_image_height);
            PixelWriter pixel_writer = writable_image.getPixelWriter();
            int[] black_array_area_one = new int[((int) new_image_width * ((height_difference / 2) + add_one))];
            Arrays.fill(black_array_area_one,0xFF000000);
            int[] black_array_area_two = new int[((int) new_image_width * (height_difference / 2))];
            Arrays.fill(black_array_area_two,0xFF000000);
            pixel_writer.setPixels(0, 0, (int) new_image_width, (height_difference / 2) + add_one, PixelFormat.getIntArgbInstance(), black_array_area_one, 0, 0);
            int[] originalData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
            pixel_writer.setPixels(0, (height_difference / 2) + add_one, (int) new_image_width, bufferedImage.getHeight(), PixelFormat.getIntArgbInstance(),originalData, 0, 0);
            pixel_writer.setPixels(0, (height_difference / 2) + add_one + bufferedImage.getHeight(),  (int) new_image_width, (height_difference / 2), PixelFormat.getIntArgbInstance(),black_array_area_two, 0, 0);
        } else {

        }
    }*/

    private BufferedImage fill_the_back_ground_with_color(BufferedImage bufferedImage, Color color) {
        double max_width = bufferedImage.getWidth() * 16D;
        double max_height = bufferedImage.getHeight() * 9D;
        BufferedImage return_me;
        if (max_width > max_height) {
            int targetWidth = bufferedImage.getWidth();
            int targetHeight = targetWidth * 16 / 9;  // Calculate the new height for a 9:16 ratio
            return_me = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = return_me.createGraphics();
            g.setColor(color);
            g.fillRect(0, 0, targetWidth, targetHeight);  // Fill the background with black
            int buffer_at_the_top = (targetHeight - bufferedImage.getHeight()) / 2;
            g.drawImage(bufferedImage, 0, buffer_at_the_top, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            g.dispose();
        } else {
            int targetWidth = bufferedImage.getHeight() * 9 / 16;
            int targetHeight = bufferedImage.getHeight();  // Calculate the new height for a 9:16 ratio
            return_me = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = return_me.createGraphics();
            g.setColor(color);
            g.fillRect(0, 0, targetWidth, targetHeight);  // Fill the background with black
            int buffer_at_the_left = (targetWidth - bufferedImage.getWidth()) / 2;
            g.drawImage(bufferedImage, buffer_at_the_left, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            g.dispose();
        }
        return return_me;
    }

    /*private Rectangle2D return_the_rectangle_2d_for_media_pool(Image image) {
        double max_width = image.getWidth() * 16D;
        double max_height = image.getHeight() * 9D;
        Rectangle2D rectangle2D;
        if (max_width > max_height) {
            double new_height = image.getWidth() * 16D / 9D;
            double offset = -((new_height - image.getHeight())/2);
            rectangle2D = new Rectangle2D(0, offset, image.getWidth(), new_height);
        } else {
            double new_width = image.getHeight() * 9D/16D;
            double offset = -((new_width - image.getWidth())/2);
            rectangle2D = new Rectangle2D(offset, 0, new_width, image.getHeight());
        }
        return rectangle2D;
    }*/

    private void create_the_time_line(HelloController helloController) {
        Pane main_pane = helloController.time_line_pane;
        Time_line_pane_data time_line_pane_data = new Time_line_pane_data();
        main_pane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(40, 40, 46), CornerRadii.EMPTY, Insets.EMPTY)));
        final double pixels_in_between_each_line = 10;
        final long time_between_every_line = TimeUnit.MILLISECONDS.toNanos(50);
        final double long_line_length = 20;
        final double half_long_line_length = 13;
        final double line_length = 7.5;
        final double line_thickness = 1.3;
        time_line_pane_data.setPixels_in_between_each_line(pixels_in_between_each_line);
        time_line_pane_data.setTime_between_every_line(time_between_every_line);
        final javafx.scene.paint.Color long_line_color = javafx.scene.paint.Color.rgb(100, 101, 103);
        final javafx.scene.paint.Color mid_line_color = javafx.scene.paint.Color.rgb(89, 95, 103);
        final javafx.scene.paint.Color short_line_color = javafx.scene.paint.Color.rgb(66, 71, 78);
        final javafx.scene.paint.Color time_text_color = javafx.scene.paint.Color.rgb(146, 146, 146);
        final javafx.scene.paint.Color time_line_indicitor_color = javafx.scene.paint.Color.rgb(206, 47, 40);
        int number_of_dividors = (int) Math.ceilDiv(get_duration(), time_between_every_line) + 1;
        double base_time_line = pixels_in_between_each_line * 11;
        double total_pane_width = (base_time_line * 2) + ((number_of_dividors - 1) * pixels_in_between_each_line);
        time_line_pane_data.setTime_line_base_line(base_time_line);
        main_pane.setUserData(time_line_pane_data);
        draw_the_rectangle_time_line_pane(0, base_time_line, main_pane.getPrefHeight(), main_pane);
        /*for (int i = 0; i < 11; i++) {
            if ((i == 1)) {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(i * pixels_in_between_each_line, half_long_line_length, mid_line_color, line_thickness));
            } else {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(i * pixels_in_between_each_line, line_length, short_line_color, line_thickness));
            }
        }*/
        /*for (int i = 0; i < number_of_dividors; i++) {
            double x_pos = (i * pixels_in_between_each_line) + base_time_line;
            if ((time_between_every_line * i) % TimeUnit.MILLISECONDS.toNanos(1000) == 0) {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, long_line_length, javafx.scene.paint.Color.RED, line_thickness));
                main_pane.getChildren().add(add_the_text_to_time_line(time_between_every_line * i, x_pos, line_length, time_text_color));
            } else if ((time_between_every_line * i) % TimeUnit.MILLISECONDS.toNanos(500) == 0) {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, half_long_line_length, javafx.scene.paint.Color.RED, line_thickness));
            } else {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, line_length, javafx.scene.paint.Color.RED, line_thickness));
            }
        }*/
        draw_the_rectangle_time_line_pane(base_time_line + (number_of_dividors - 1) * pixels_in_between_each_line, base_time_line, main_pane.getPrefHeight(), main_pane);
        /*double base_line_for_the_end_rectangle = base_time_line + (number_of_dividors) * pixels_in_between_each_line;
        for (int i = 0; i < 11; i++) {
            double x_pos = i * pixels_in_between_each_line + base_line_for_the_end_rectangle;
            if ((time_between_every_line * (i + number_of_dividors)) % TimeUnit.MILLISECONDS.toNanos(1000) == 0) {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, long_line_length, long_line_color, line_thickness));
                if (i == 0) {
                    main_pane.getChildren().add(add_the_text_to_time_line(time_between_every_line * (number_of_dividors + i), x_pos, line_length, time_text_color));
                }
            } else if ((time_between_every_line * (i + number_of_dividors)) % TimeUnit.MILLISECONDS.toNanos(500) == 0) {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, half_long_line_length, mid_line_color, line_thickness));
            } else {
                main_pane.getChildren().add(draw_the_line_on_the_time_line(x_pos, line_length, short_line_color, line_thickness));
            }
        }*/
        time_line_pane_data.setTime_line_end_base_line(base_time_line + (number_of_dividors - 1) * pixels_in_between_each_line);
        set_up_the_verses_time_line(helloController, main_pane, base_time_line, time_between_every_line);
        Polygon polygon = set_up_the_polygon_for_the_overlaying_pane(helloController, base_time_line, time_line_indicitor_color, main_pane);
        time_line_pane_data.setPolygon(polygon);
        //set_up_time_line_indicator(main_pane, base_time_line, time_line_indicitor_color);
        listen_to_time_line_clicked(helloController, main_pane);
        listen_to_mouse_movement_over_time_line_indicator(helloController, main_pane, polygon);
        //listen_to_mouse_moving_in_time_line_pane(main_pane);
    }

    private void draw_the_rectangle_time_line_pane(double start_x, double width, double height, Pane pane) {
        Rectangle rectangle = new Rectangle(start_x, 0, width, height);
        rectangle.setStrokeWidth(0);
        rectangle.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.25));
        pane.getChildren().add(rectangle);
    }

    private Line draw_the_line_on_the_time_line(double x_pos, double line_length, javafx.scene.paint.Color color, double width) {
        Line line_to_separate = new Line(x_pos, 0, x_pos, line_length);
        line_to_separate.setStroke(color);
        line_to_separate.setStrokeWidth(width);
        //line_to_separate.setSmooth(false);
        return line_to_separate;
    }

    private Text add_the_text_to_time_line(long millisecond, double line_end, double line_length, javafx.scene.paint.Color color) {
        Text time_label = new Text(convertnanosecondsToTime(millisecond));
        //time_label.setSmooth(false);
        time_label.setFont(new Font(11));
        Bounds bounds = time_label.getLayoutBounds();
        time_label.setX(line_end + 5);
        time_label.setY(line_length - bounds.getMinY() + 2.5);
        time_label.setFill(color);
        return time_label;
    }

    private String convertnanosecondsToTime(long nanoseconds) {
        long milliseconds = nanoseconds / 1_000_000L;
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private void set_up_the_verses_time_line(HelloController helloController, Pane pane, double pixels_in_between_each_line, long time_between_every_line) {
        //double adjustor = pixels_in_between_each_line / time_between_every_line;
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        StackPane[] array_of_verse_stack_panes = new StackPane[ayats_processed.size()];
        for (int i = 0; i < ayats_processed.size(); i++) {
            StackPane stackPane = return_the_stack_pane_for_the_verse_rectangle(i, time_line_pane_data.getTime_line_base_line() + (nanoseconds_to_pixels(time_line_pane_data, ayats_processed.get(i).getStart_millisecond())), nanoseconds_to_pixels(time_line_pane_data, ayats_processed.get(i).getDuration()));
            pane.getChildren().add(stackPane);
            array_of_verse_stack_panes[i] = stackPane;
            ayats_processed.get(i).setStack_pane_hosting_rectangle(stackPane);
        }
        StackPane empty_stack_pane = new StackPane();
        Rectangle empty_rectangle = new Rectangle();
        if (sound_mode == Sound_mode.UPLOADED) {
            for (int i = 0; i < array_of_verse_stack_panes.length; i++) {
                if (i == 0 && i == array_of_verse_stack_panes.length - 1) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, i, array_of_verse_stack_panes[i], (Rectangle) array_of_verse_stack_panes[i].getChildren().getFirst(), empty_stack_pane, empty_rectangle, empty_stack_pane, empty_rectangle, Verse_position_mode.START_AND_END);
                } else if (i == 0) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, i, array_of_verse_stack_panes[i], (Rectangle) array_of_verse_stack_panes[i].getChildren().getFirst(), empty_stack_pane, empty_rectangle, array_of_verse_stack_panes[i + 1], (Rectangle) array_of_verse_stack_panes[i + 1].getChildren().getFirst(), Verse_position_mode.START);
                } else if (i == array_of_verse_stack_panes.length - 1) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, i, array_of_verse_stack_panes[i], (Rectangle) array_of_verse_stack_panes[i].getChildren().getFirst(), array_of_verse_stack_panes[i - 1], (Rectangle) array_of_verse_stack_panes[i - 1].getChildren().getFirst(), empty_stack_pane, empty_rectangle, Verse_position_mode.END);
                } else {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, i, array_of_verse_stack_panes[i], (Rectangle) array_of_verse_stack_panes[i].getChildren().getFirst(), array_of_verse_stack_panes[i - 1], (Rectangle) array_of_verse_stack_panes[i - 1].getChildren().getFirst(), array_of_verse_stack_panes[i + 1], (Rectangle) array_of_verse_stack_panes[i + 1].getChildren().getFirst(), Verse_position_mode.MIDDLE);
                }
            }
        }
    }

    private StackPane return_the_stack_pane_for_the_verse_rectangle(int local_selected_verse, double start_x, double duration_in_pixels) {
        Label verse_text = new Label("Verse ".concat(String.valueOf(ayats_processed.get(local_selected_verse).getVerse_number())));
        verse_text.setPadding(new Insets(0, 10, 0, 10));
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(duration_in_pixels);
        stackPane.setMinWidth(duration_in_pixels);
        stackPane.setMaxWidth(duration_in_pixels);
        stackPane.setPrefHeight(30);
        stackPane.setLayoutX(start_x);
        stackPane.setLayoutY(30);
        Rectangle rectangle = new Rectangle(duration_in_pixels, 20);
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(javafx.scene.paint.Color.BLACK);
        rectangle.setArcHeight(5);
        rectangle.setArcWidth(5);
        rectangle.setFill(javafx.scene.paint.Color.WHITE);
        stackPane.getChildren().addAll(rectangle, verse_text);
        return stackPane;
    }

    private void set_up_time_line_indicator(Pane pane, double start_x, javafx.scene.paint.Color color) {
        final double full_length = 12.5D;
        final double partial_length = 7.5D;
        final double rectangle_width = 2.5D;
        final double time_line_indicator_width = 15D;
        final double right_side_of_rectangle = ((time_line_indicator_width - rectangle_width) / 2) + rectangle_width;
        final double left_side_of_rectangle = (time_line_indicator_width - rectangle_width) / 2;
        final double full_pane_height = pane.getHeight();
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        Polygon polygon = new Polygon();
        polygon.setSmooth(true);
        polygon.getPoints().addAll(
                0D, 0D,     // Top-left
                time_line_indicator_width, 0D,
                time_line_indicator_width, partial_length,
                right_side_of_rectangle, full_length,
                right_side_of_rectangle, full_pane_height,
                left_side_of_rectangle, full_pane_height,
                left_side_of_rectangle, full_length,
                0D, partial_length
        );
        //polygon.setFill(javafx.scene.paint.Color.rgb(206, 47, 40));
        polygon.setFill(color);
        polygon.setLayoutX(start_x - (time_line_indicator_width / 2));
        polygon.setLayoutY(0);
        pane.getChildren().add(polygon);
        //time_line_pane_data.setPolygon_width(time_line_indicator_width);
    }

    private void update_the_time_line_indicator(HelloController helloController, long milliseconds) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        double new_x = return_the_real_x_position_based_on_time(helloController, milliseconds);
        polygon_data.setReal_polygon_position(new_x);
        draw_the_polygon_time_line(helloController, new_x);
    }

    private double return_the_real_x_position_based_on_time(HelloController helloController, long milliseconds) {
        Pane pane_holding_the_time_line = helloController.time_line_pane;
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane_holding_the_time_line.getUserData();
        double pixels_in_between_each_line = time_line_pane_data.getPixels_in_between_each_line();
        long time_between_every_line = time_line_pane_data.getTime_between_every_line();
        double adjustor = pixels_in_between_each_line / time_between_every_line;
        double time_line_base_line = time_line_pane_data.getTime_line_base_line();
        double half_polygon = polygon_data.getPolygon_width() / 2;
        return (milliseconds * adjustor) - half_polygon + time_line_base_line;
    }

    private void listen_to_time_line_clicked(HelloController helloController, Pane pane) {
        Time_line_pane_data time_line_pane_data = ((Time_line_pane_data) pane.getUserData());
        double y_drag_area = time_line_pane_data.getMouse_drag_y_area();
        double base_time_line = time_line_pane_data.getTime_line_base_line();
        double end_time_line = time_line_pane_data.getTime_line_end_base_line();
        empty_tile_pane_context_menu = new ContextMenu();
        MenuItem item1 = new MenuItem("Upload media");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                upload_media_has_been_clicked(helloController);
            }
        });
        empty_tile_pane_context_menu.getItems().addAll(item1);
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    empty_tile_pane_context_menu.hide();
                    if (mouseEvent.getY() <= y_drag_area && mouseEvent.getX() >= base_time_line && mouseEvent.getX() <= end_time_line) {
                        time_line_clicked(helloController, pane, mouseEvent.getX());
                        update_the_verse_based_on_milliseconds(helloController, pixels_to_nanoseconds(time_line_pane_data, mouseEvent.getX() - time_line_pane_data.getTime_line_base_line()));
                        set_up_everything_image_view_time_line_x_pos(helloController, mouseEvent.getX(), Type_of_Image.FULL_QUALITY);
                    }
                    helloController.list_view_with_all_of_the_languages.refresh();
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    empty_tile_pane_context_menu.show(helloController.tile_pane_media_pool, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                send_analytics_event("time_line_clicked");
            }
        });
    }

    private void time_line_clicked(HelloController helloController, Pane pane, double x_position) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        Polygon polygon = time_line_pane_data.getPolygon();
        Polygon_data polygon_data = (Polygon_data) time_line_pane_data.getPolygon().getUserData();
        double base_time_line = time_line_pane_data.getTime_line_base_line();
        double half_polygon = polygon_data.getPolygon_width() / 2;
        double end_time_line = time_line_pane_data.getTime_line_end_base_line();
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            pause_the_media_player(helloController);
        }
        if (x_position < base_time_line) {
            seek_media_based_on_time_line_changed(pane, 0);
            draw_the_polygon_time_line(helloController, base_time_line - half_polygon);
            set_the_polygon_real_start(polygon, base_time_line - half_polygon);
            return;
        }
        if (x_position > end_time_line) {
            seek_media_based_on_time_line_changed(pane, end_time_line - base_time_line);
            draw_the_polygon_time_line(helloController, end_time_line - half_polygon);
            set_the_polygon_real_start(polygon, end_time_line - half_polygon);
            return;
        }
        seek_media_based_on_time_line_changed(pane, x_position - base_time_line);
        draw_the_polygon_time_line(helloController, x_position - half_polygon);
        set_the_polygon_real_start(polygon, x_position - half_polygon);

    }

    private void listen_to_mouse_movement_over_time_line_indicator(HelloController helloController, Pane pane, Polygon polygon) {
        Time_line_pane_data time_line_pane_data = ((Time_line_pane_data) pane.getUserData());
        Polygon_data polygon_data = (Polygon_data) time_line_pane_data.getPolygon().getUserData();
        double y_area = time_line_pane_data.getMouse_drag_y_area();
        double half_polygon = polygon_data.getPolygon_width() / 2;
        polygon.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (polygon.getCursor() == null || !polygon.getCursor().equals(Cursor.CLOSED_HAND)) {
                    polygon.setCursor(Cursor.OPEN_HAND);
                }
            }
        });
        polygon.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (polygon.getCursor() == null || !polygon.getCursor().equals(Cursor.CLOSED_HAND)) {
                    polygon.setCursor(Cursor.DEFAULT);
                }
            }
        });
        polygon.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                polygon.setCursor(Cursor.CLOSED_HAND);
                send_analytics_event("time_line_polygon_clicked");
            }
        });
        polygon.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Node parent_of_the_polygon = polygon.getParent();
                Point2D local = parent_of_the_polygon.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                if (polygon.contains(polygon.parentToLocal(local))) {
                    polygon.setCursor(Cursor.OPEN_HAND);
                } else {
                    polygon.setCursor(Cursor.DEFAULT);
                }
                Point2D point_2d_relative_to_the_time_line = helloController.time_line_pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                set_up_everything_image_view_time_line_x_pos(helloController, point_2d_relative_to_the_time_line.getX(), Type_of_Image.FULL_QUALITY);
                helloController.list_view_with_all_of_the_languages.refresh();
            }
        });
        polygon.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double x_position = pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
                long time_of_x_pos_in_nano_seconds = pixels_to_nanoseconds(time_line_pane_data, x_position - time_line_pane_data.getTime_line_base_line());
                time_line_clicked(helloController, pane, x_position);
                if (x_position - time_line_pane_data.getTime_line_base_line() >= 0) {
                    update_the_verse_based_on_milliseconds(helloController, time_of_x_pos_in_nano_seconds);
                }
                set_up_everything_image_view_time_line_x_pos(helloController, x_position, Type_of_Image.THUMBNAIL_QUALITY);
                /*set_the_chatgpt_image_view(helloController, return_the_image_on_click(pane, x_position), Type_of_Image.THUMBNAIL_QUALITY);
                Shape_object_time_line shape_object_time_line = return_the_shape_on_click(pane, x_position);
                if (shape_object_time_line != null) {
                    set_up_the_image_controller_enable_disable(helloController, return_the_shape_on_click(pane, x_position));
                    set_the_opacity_of_image_view_considering_everything(helloController, shape_object_time_line);
                } else {
                    detach_the_listeners_image_control_unknown(helloController);
                    disable_the_image_control_panel(helloController);
                    reset_the_opacity(helloController);
                }*/
            }
        });
    }

    private void set_up_the_image_controller_enable_disable(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        detach_the_listeners_image_control_unknown(helloController);
        if (shape_object_time_line == null) {
            disable_the_image_control_panel(helloController);
        } else {
            create_and_attach_all_the_image_listeners(helloController, shape_object_time_line);
            enable_the_image_control_panel(helloController, shape_object_time_line);
        }
    }

    private void detach_the_listeners_image_control_unknown(HelloController helloController) {
        for (Listener_info listener_info : array_list_with_all_of_the_image_control_listeners) {
            if (listener_info.getListener_type() == Listener_type.OPACITY_LISTENER) {
                if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
                    helloController.slider_to_control_the_opacity_of_an_image.valueProperty().removeListener(listener_info.getChange_listener());
                    listener_info.setListener_attached(false);
                }
            } else if (listener_info.getListener_type() == Listener_type.FADE_IN_LISTENER) {
                if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
                    helloController.slider_to_control_fade_in_of_image.valueProperty().removeListener(listener_info.getChange_listener());
                    listener_info.setListener_attached(false);
                }
            } else if (listener_info.getListener_type() == Listener_type.FADE_OUT_LISTENER) {
                if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
                    helloController.slider_to_control_fade_out_of_image.valueProperty().removeListener(listener_info.getChange_listener());
                    listener_info.setListener_attached(false);
                }
            }
        }
        array_list_with_all_of_the_image_control_listeners.clear();
    }


   /* private void listen_to_mouse_moving_in_time_line_pane(Pane pane, Polygon polygon) {
        Time_line_pane_data time_line_pane_data = ((Time_line_pane_data) pane.getUserData());
        double y_area = time_line_pane_data.getMouse_drag_y_area();
        double base_time_line = time_line_pane_data.getTime_line_base_line();
        double end_time_line = time_line_pane_data.getTime_line_end_base_line();
        pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getY() <= y_area && mouseEvent.getX() >= base_time_line && mouseEvent.getX() <= end_time_line) {
                    if (polygon.getCursor() == null || !polygon.getCursor().equals(Cursor.CLOSED_HAND)) {
                        pane.setCursor(Cursor.HAND);
                    }
                } else {
                    if (polygon.getCursor() == null || !polygon.getCursor().equals(Cursor.CLOSED_HAND)) {
                        pane.setCursor(Cursor.DEFAULT);
                    }
                }
            }
        });
    }*/

    private void seek_media_based_on_time_line_changed(Pane pane, double x_position) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double pixels_in_between_each_line = time_line_pane_data.getPixels_in_between_each_line();
        long time_between_every_line = time_line_pane_data.getTime_between_every_line();
        double adjustor = time_between_every_line / pixels_in_between_each_line;
        mediaPlayer.seek(new Duration(TimeUnit.NANOSECONDS.toMillis((long) (x_position * adjustor))));
    }

    private void animation_timer(HelloController helloController) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING && lastKnownSystemTime > 0) {
                    Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                    double elapsed = (System.currentTimeMillis() - lastKnownSystemTime); // seconds
                    long time_in_nanos = (lastKnownMediaTime + TimeUnit.MILLISECONDS.toNanos((long) elapsed));
                    double x_position_of_polygon = return_the_real_x_position_based_on_time(helloController, time_in_nanos);
                    set_the_scroll_pane_h_value_auto_scroll(helloController, return_the_real_x_position_based_on_time(helloController, time_in_nanos));
                    make_the_time_line_in_the_middle(helloController, x_position_of_polygon);
                    is_it_time_to_change_verses(helloController, time_in_nanos);
                    if (System.currentTimeMillis() - last_text_update_time > 50) {
                        loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
                        last_text_update_time = System.currentTimeMillis();
                    }
                    if (last_seen_image_vid_is_playing == null || time_in_nanos > last_seen_image_vid_is_playing.getEnd_time()) {
                        last_seen_image_vid_is_playing = return_the_shape_on_click(helloController.time_line_pane, nanoseconds_to_pixels(time_line_pane_data, time_in_nanos) + time_line_pane_data.getTime_line_base_line());
                        if (last_seen_image_vid_is_playing == null) {
                            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
                            detach_the_listeners_image_control_unknown(helloController);
                            disable_the_image_control_panel(helloController);
                            reset_the_opacity(helloController);
                        } else {
                            set_the_chatgpt_image_view(helloController, last_seen_image_vid_is_playing.getImage_id(), Type_of_Image.FULL_QUALITY);
                            set_up_the_image_controller_enable_disable(helloController, last_seen_image_vid_is_playing);
                        }
                    }
                    if (last_seen_image_vid_is_playing != null) {
                        set_the_opacity_of_image_view_considering_everything(helloController, last_seen_image_vid_is_playing);
                    }
                }
            }
        };
    }

    private void listen_to_give_feed_back_button(HelloController helloController) {
        helloController.give_feedback_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //showEmailPopupWithReply();
                show_feedback_settings_dialog("Feedback");
                send_analytics_event("feedback_clicked");
            }
        });
    }

    private void showEmailPopupWithReply() {
        Stage feedbackStage = new Stage();
        feedbackStage.initOwner(main_stage);
        feedbackStage.initStyle(StageStyle.DECORATED);
        //feedbackStage.initModality(Modality.WINDOW_MODAL); // optional
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label main_text = new Label("If you have any feedback, suggestions or bug reports, please send them to the email below. ");
        Label email_address = new Label(help_email);
        JFXButton copy_button = new JFXButton("Copy email address");

        VBox.setMargin(main_text, new Insets(0, 10, 0, 10));
        VBox.setMargin(email_address, new Insets(10, 10, 0, 10));
        VBox.setMargin(copy_button, new Insets(25, 10, 0, 10));
        main_text.setAlignment(Pos.CENTER);
        main_text.setTextAlignment(TextAlignment.CENTER); // center each line of text
        main_text.setWrapText(true);
        copy_button.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
        copy_button.setFocusTraversable(false);

        copy_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                copyToClipboard(help_email);
                showToast(feedbackStage, "Copied", 2500);
            }
        });
        vBox.getChildren().addAll(main_text, email_address, copy_button);
        Scene scene = new Scene(vBox, 450, 225);
        feedbackStage.setScene(scene);
        feedbackStage.setTitle("Send Feedback");
        feedbackStage.show();
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private void showToast(Stage ownerStage, String message, int durationMillis) {
        Popup popup = new Popup();
        Label toastLabel = new Label(message);
        toastLabel.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.75); " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;" +
                        "-fx-font-size: 12px;"
        );
        toastLabel.setOpacity(0.9);
        popup.getContent().add(toastLabel);
        popup.setAutoHide(true);

        // Centered at bottom of the window
        popup.show(ownerStage);
        popup.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - toastLabel.getWidth() / 2);
        popup.setY(ownerStage.getY() + 0.925 * ownerStage.getHeight() - toastLabel.getHeight());

        // Hide after duration
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(durationMillis), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                popup.hide();
            }
        }));
        timeline.play();
    }

    private void set_the_scroll_pane_h_value_auto_scroll(HelloController helloController, double x_position) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        ScrollPane scrollPane = helloController.scroll_pane_hosting_the_time_line;
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double contentWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
        double contentWidth_for_h_value = contentWidth - viewportWidth;
        double half_visible_width = viewportWidth / 2;
        double half_polygon_width = polygon_data.getPolygon_width() / 2;
        Polygon_mode_time_line polygon_mode_time_line = return_the_polygon_status_according_to_the_time_line(x_position, half_polygon_width, half_visible_width, contentWidth);
        if (polygon_mode_time_line == Polygon_mode_time_line.BEFORE_MIDDLE) {
            scrollPane.setHvalue(0);
        } else if (polygon_mode_time_line == Polygon_mode_time_line.AFTER_MIDDLE) {
            scrollPane.setHvalue(1);
        } else {
            double h_value = (x_position - half_visible_width) / (contentWidth_for_h_value);
            scrollPane.setHvalue(h_value);
        }
    }

    private void make_the_time_line_in_the_middle(HelloController helloController, double x_position) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        ScrollPane scrollPane = helloController.scroll_pane_hosting_the_time_line;
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double contentWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
        double half_visible_width = viewportWidth / 2;
        double half_polygon_width = polygon_data.getPolygon_width() / 2;
        Polygon_mode_time_line polygon_mode_time_line = return_the_polygon_status_according_to_the_time_line(x_position, half_polygon_width, half_visible_width, contentWidth);
        if (polygon_mode_time_line == Polygon_mode_time_line.BEFORE_MIDDLE) {
            draw_the_polygon_time_line(helloController, x_position);
            polygon_data.setReal_polygon_position(x_position);
        } else if (polygon_mode_time_line == Polygon_mode_time_line.AFTER_MIDDLE) {
            draw_the_polygon_time_line(helloController, x_position);
            polygon_data.setReal_polygon_position(x_position);
        } else {
            set_the_polygon_to_the_middle_when_video_is_playing(helloController);
            polygon_data.setReal_polygon_position(x_position);
        }
    }

    /*private void force_the_time_line_indicator_to_be_at_the_middle(ScrollPane scrollPane, double x_position) {
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double contentWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
        double contentWidth_for_h_value = contentWidth - viewportWidth;
        double half_visible_width = viewportWidth / 2;
        double h_value = (x_position - half_visible_width) / (contentWidth_for_h_value);
        if (h_value < 0) {
            h_value = 0;
        }
        if (h_value > 1) {
            h_value = 1;
        }
        scrollPane.setHvalue(h_value);
    }*/

    private void pause_the_media_player(HelloController helloController) {
        mediaPlayer.pause();
    }

    private void play_the_media_player(HelloController helloController) {
        mediaPlayer.play();
    }

    private void update_the_verse_based_on_milliseconds(HelloController helloController, long milliseconds) {
        selected_verse = which_verse_am_i_on_milliseconds(milliseconds);
        the_verse_changed(helloController, selected_verse);
    }

    private int which_verse_am_i_on_milliseconds(long milliseconds) {
        Verse_class_final verse_class_final_with_current_ms_to_be_comapred = new Verse_class_final();
        verse_class_final_with_current_ms_to_be_comapred.setStart_millisecond(milliseconds);
        int index = Collections.binarySearch(ayats_processed, verse_class_final_with_current_ms_to_be_comapred, new Comparator<Verse_class_final>() {
            @Override
            public int compare(Verse_class_final object_1, Verse_class_final object_2) {
                return Long.compare(object_1.getStart_millisecond(), object_2.getStart_millisecond()); // or a.getMilliseconds()
            }
        });
        if (index >= 0) {
            return index;
        } else {
            return selected_verse = (index * -1) - 2;
        }
    }

    private double nanoseconds_to_pixels(Time_line_pane_data time_line_pane_data, long milliseconds) {
        double adjustor = time_line_pane_data.getPixels_in_between_each_line() / time_line_pane_data.getTime_between_every_line();
        return milliseconds * adjustor;
    }

    private long pixels_to_nanoseconds(Time_line_pane_data time_line_pane_data, double pixels) {
        double adjustor = time_line_pane_data.getTime_between_every_line() / time_line_pane_data.getPixels_in_between_each_line();
        return (long) (pixels * adjustor);
    }

    private void is_it_time_to_change_verses(HelloController helloController, double milliseconds) {
        if (ayats_processed.size() - 1 == selected_verse) {
            return;
        }
        if (milliseconds > ayats_processed.get(selected_verse + 1).getStart_millisecond()) {
            selected_verse++;
            the_verse_changed(helloController, selected_verse);
        }
    }

    private void mediaPlayer_status_changed(HelloController helloController) {
        mediaPlayer.statusProperty().addListener(new ChangeListener<MediaPlayer.Status>() {
            @Override
            public void changed(ObservableValue<? extends MediaPlayer.Status> observableValue, MediaPlayer.Status old_status, MediaPlayer.Status new_status) {
                if (new_status.equals(MediaPlayer.Status.READY)) {
                    media_is_ready(helloController);
                } else if (new_status.equals(MediaPlayer.Status.PLAYING)) {
                    last_seen_image_vid_is_playing = null;
                    lastKnownSystemTime = 0;
                    timer.start();
//                    set_the_play_pause_button(helloController, "pause");
                    set_the_play_pause_button(helloController, Audio_status.PAUSED);
                    if (old_status != null && old_status.equals(MediaPlayer.Status.STOPPED)) {
                        selected_verse = 0;
                        the_verse_changed(helloController, selected_verse);
                    }
                    set_the_status_of_locked_in_polygon(helloController, true);
                } else if (new_status.equals(MediaPlayer.Status.PAUSED)) {
                    timer.stop();
//                    set_the_play_pause_button(helloController, "play");
                    set_the_play_pause_button(helloController, Audio_status.PLAYING);
                    set_the_status_of_locked_in_polygon(helloController, false);
                } else if (new_status.equals(MediaPlayer.Status.STOPPED)) {
                    set_the_status_of_locked_in_polygon(helloController, false);
                }
            }
        });
    }

    private void convert_png_to_jpg(File old_file, File new_file) {
        DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
        String ffmpegPath = locator.getExecutablePath();
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-i", old_file.getAbsolutePath(),
                "-vf", "format=yuva444p,geq='if(lte(alpha(X,Y),16),255,p(X,Y))':'if(lte(alpha(X,Y),16),128,p(X,Y))':'if(lte(alpha(X,Y),16),128,p(X,Y))'",
                new_file.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
            process.getInputStream().transferTo(OutputStream.nullOutputStream());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("FFmpeg failed with exit code " + exitCode);
                show_alert("Audio encoding failed. FFMPEG");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean am_i_in_time_line_boundries(HelloController helloController, double x_pos, double y_pos, Media_pool_item_dragged media_pool_item_dragged) {
        Point2D scene_x_and_y = helloController.scroll_pane_hosting_the_time_line.localToScene(0, 0);
        double min_x = scene_x_and_y.getX();
        double max_x = min_x + helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
        double min_y = scene_x_and_y.getY();
        double max_y = min_y + helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getHeight();
        if (x_pos >= min_x && x_pos <= max_x && y_pos >= min_y && y_pos <= max_y) {
            media_pool_item_dragged.setHas_this_been_dragged(true);
            return true;
        } else {
            return false;
        }
    }

    private Rectangle create_and_return_time_line_rectangle(Pane pane, double start_x_pos, double width) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double height = 60;
        /*x_pos -= (width / 2);
        x_pos = Math.max(x_pos, time_line_pane_data.getTime_line_base_line());
        if (x_pos + width >= time_line_pane_data.getTime_line_end_base_line()) {
            x_pos = time_line_pane_data.getTime_line_end_base_line() - width;
        }*/
        Rectangle rectangle;
        rectangle = new Rectangle(width, height);
        rectangle.setY(60);
        rectangle.setX(start_x_pos);
        pane.getChildren().add(pane.getChildren().size() - 1, rectangle);
        return rectangle;
    }

    private double move_the_rectangle(Pane pane, Rectangle rectangle, double x_pos) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double width = rectangle.getWidth();
        x_pos -= (width / 2);
        x_pos = Math.max(x_pos, time_line_pane_data.getTime_line_base_line());
        if (x_pos + width >= time_line_pane_data.getTime_line_end_base_line()) {
            x_pos = time_line_pane_data.getTime_line_end_base_line() - width;
        }
        rectangle.setX(x_pos);
        return x_pos;
    }

    private void remove_the_image_from_time_line_hash_map(Pane pane, Shape_object_time_line shapeObjectTimeLine) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        time_line_pane_data.getTree_set_containing_all_of_the_items().remove(shapeObjectTimeLine);
        pane.getChildren().remove(shapeObjectTimeLine.getShape());
    }

    private void set_up_the_image_rectangle(Rectangle rectangle, Image image, Pane pane, Opacity_settings opacity_settings) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        ImagePattern pattern = new ImagePattern(create_image_for_the_time_line_thumbnail(remove_transparent_pixels(image), (int) rectangle.getWidth(), (int) rectangle.getHeight(), opacity_settings, time_line_pane_data), 0, 0, 1, 1, true);
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(javafx.scene.paint.Color.BLACK);
        rectangle.setArcHeight(5);
        rectangle.setArcWidth(5);
        rectangle.setFill(pattern);
    }

    private Image create_image_for_the_time_line_thumbnail(Image image, int width, int height, Opacity_settings opacity_settings, Time_line_pane_data time_line_pane_data) {
        try {
            BufferedImage thumbnail = Thumbnails.of(image_to_buffered_image(image))
                    .height(height)
                    .keepAspectRatio(true)
                    .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                    .outputQuality(1)
                    .asBufferedImage();
            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            PixelReader pixelReader = buffer_image_to_image(thumbnail).getPixelReader();
            int number_of_images = Math.floorDiv(width, thumbnail.getWidth());
            if (opacity_settings.getFade_in() == 0 && opacity_settings.getFade_out() == 0) {
                for (int k = 0; k < number_of_images; k++) {
                    pixelWriter.setPixels(k * thumbnail.getWidth(), 0, thumbnail.getWidth(), thumbnail.getHeight(), pixelReader, 0, 0);
                }
                if (width % thumbnail.getWidth() != 0) {
                    pixelWriter.setPixels(number_of_images * thumbnail.getWidth(), 0, width % thumbnail.getWidth(), thumbnail.getHeight(), pixelReader, 0, 0);
                }
            } else {
                for (int i = 0; i < width; i++) {
                    double time_since_start = pixels_to_nanoseconds(time_line_pane_data, i);
                    double time_till_end = pixels_to_nanoseconds(time_line_pane_data, width - i);
                    double fade_in_multiplier = Math.min(1, time_since_start / (opacity_settings.getFade_in() * TimeUnit.SECONDS.toNanos(1)));
                    double fade_out_multiplier = Math.min(1, time_till_end / (opacity_settings.getFade_out() * TimeUnit.SECONDS.toNanos(1)));
                    for (int j = 0; j < height; j++) {
                        int argb_color = pixelReader.getArgb(i % thumbnail.getWidth(), j % thumbnail.getHeight());
                        int alpha = (int) (fade_in_multiplier * fade_out_multiplier * 255);
                        alpha <<= 24;
                        alpha |= (0x00FFFFFF & argb_color);
                        pixelWriter.setArgb(i, j, alpha);
                    }
                }
            }
            return writableImage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Image remove_transparent_pixels(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int start_left_to_right = -1;
        int end_left_to_right = -1;
        int start_top_to_bottom = -1;
        int end_top_to_bottom = -1;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (!pixelReader.getColor(j, i).equals(javafx.scene.paint.Color.TRANSPARENT)) {
                    if (start_left_to_right == -1) {
                        start_left_to_right = j;
                    }
                    if (j > end_left_to_right) {
                        end_left_to_right = j;
                    }
                    if (start_top_to_bottom == -1) {
                        start_top_to_bottom = i;
                    }
                    if (i > end_top_to_bottom) {
                        end_top_to_bottom = i;
                    }
                }
            }
        }
        if (end_left_to_right - start_left_to_right == 0 || end_top_to_bottom - start_top_to_bottom == 0) {

        }
        WritableImage new_image_to_be_returned = new WritableImage(end_left_to_right - start_left_to_right + 1, end_top_to_bottom - start_top_to_bottom + 1);
        PixelWriter pixelWriter = new_image_to_be_returned.getPixelWriter();
        int x_counter = 0;
        int y_counter = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            boolean found_a_non_transparent_pixel = false;
            for (int j = 0; j < image.getWidth(); j++) {
                if (!pixelReader.getColor(j, i).equals(javafx.scene.paint.Color.TRANSPARENT)) {
                    pixelWriter.setColor(x_counter, y_counter, pixelReader.getColor(j, i));
                    found_a_non_transparent_pixel = true;
                    x_counter++;
                }
            }
            x_counter = 0;
            if (found_a_non_transparent_pixel) {
                y_counter++;
            }
        }
        return new_image_to_be_returned;
    }

    private void configure_the_image_rectangle(Shape_object_time_line shapeObjectTimeLine, HelloController helloController, Pane pane, Image image) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Remove media");
        contextMenu.getItems().add(item1);
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                remove_the_image_from_time_line_hash_map(pane, shapeObjectTimeLine);
                clear_image_view_if_recatngle_is_in_boundries(helloController, pane, shapeObjectTimeLine);
                disable_image_control_if_its_in_the_boundaries(helloController, pane, shapeObjectTimeLine);
                reset_the_opacity_if_its_not_in_boundaries(helloController, pane, shapeObjectTimeLine);
            }
        });
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        Rectangle rectangle = (Rectangle) shapeObjectTimeLine.getShape();
        double change_cursor_to_double_arrow_buffer = 10;
        final boolean[] new_dragged_rectangle_created_already = {false};
        rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    contextMenu.hide();
                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(rectangle, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        });
        rectangle.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                set_the_rectangle_mouse_cursor(helloController, mouseEvent.getSceneX(), mouseEvent.getSceneY(), shapeObjectTimeLine, change_cursor_to_double_arrow_buffer);
            }
        });
        rectangle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                send_analytics_event("image_rectanlge_clicked");
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    double mouse_scene_x_translated = helloController.time_line_pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
                    double local_x = mouse_scene_x_translated - shapeObjectTimeLine.getStart();
                    TreeSet<Shape_object_time_line> copy_of_all_items_tree_set = new TreeSet<>(time_line_pane_data.getTree_set_containing_all_of_the_items());
                    copy_of_all_items_tree_set.remove(shapeObjectTimeLine);
                    double polygon_pos = return_polygon_middle_position(time_line_pane_data);
                    boolean did_this_ever_change;
                    if (rectangle.getX() <= polygon_pos && rectangle.getX() + rectangle.getWidth() >= polygon_pos) {
                        did_this_ever_change = true;
                    } else {
                        did_this_ever_change = false;
                    }
                    if (get_type_of_movement(rectangle, local_x, change_cursor_to_double_arrow_buffer) == MovementType.START) {
                        Rectangle_changed_info rectangleChangedInfo = new Rectangle_changed_info(mouseEvent.getSceneX(), MovementType.START, shapeObjectTimeLine.getStart(), shapeObjectTimeLine.getEnd(), local_x, shapeObjectTimeLine.getImage_id(), copy_of_all_items_tree_set, did_this_ever_change);
                        rectangle.setUserData(rectangleChangedInfo);
                    } else if (get_type_of_movement(rectangle, local_x, change_cursor_to_double_arrow_buffer) == MovementType.END) {
                        Rectangle_changed_info rectangleChangedInfo = new Rectangle_changed_info(mouseEvent.getSceneX(), MovementType.END, shapeObjectTimeLine.getStart(), shapeObjectTimeLine.getEnd(), local_x, shapeObjectTimeLine.getImage_id(), copy_of_all_items_tree_set, did_this_ever_change);
                        rectangle.setUserData(rectangleChangedInfo);
                    } else {
                        Rectangle fake_rectanlge_less_opacity = deepCopyRectangle(rectangle);
                        fake_rectanlge_less_opacity.setOpacity(0.4D);
                        fake_rectanlge_less_opacity.setVisible(false);
                        pane.getChildren().add(fake_rectanlge_less_opacity);
                        Rectangle_changed_info rectangleChangedInfo = new Rectangle_changed_info(mouseEvent.getSceneX(), MovementType.MIDDLE, shapeObjectTimeLine.getStart(), shapeObjectTimeLine.getEnd(), local_x, fake_rectanlge_less_opacity, shapeObjectTimeLine.getImage_id(), copy_of_all_items_tree_set, did_this_ever_change);
                        rectangle.setUserData(rectangleChangedInfo);
                        rectangle.setCursor(Cursor.CLOSED_HAND);
                    }
                }
            }
        });
        rectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Rectangle_changed_info rectangleChangedInfo = (Rectangle_changed_info) rectangle.getUserData();
                    double mouse_scene_x_translated = helloController.time_line_pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
                    double local_x = rectangleChangedInfo.getRelative_x();
                    if (rectangleChangedInfo.getType_of_movement() == MovementType.START) {
                        double mouse_difference = rectangleChangedInfo.getOriginal_x() - mouseEvent.getSceneX();
                        double new_start = rectangleChangedInfo.getOriginal_start_rectangle() - mouse_difference;
                        double new_width = rectangleChangedInfo.getOriginal_end_rectangle() - new_start;
                        if (rectangleChangedInfo.getOriginal_start_rectangle() - mouse_difference >= time_line_pane_data.getTime_line_base_line() && new_width >= min_rectnagle_width) {
                            double[] collision_result = return_the_collision(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), rectangleChangedInfo.getOriginal_start_rectangle() - mouse_difference, rectangleChangedInfo.getOriginal_end_rectangle(), CollisionSearchType.Start);
                            if (collision_result[0] < 0) {
                                rectangle.setX(rectangleChangedInfo.getOriginal_start_rectangle() - mouse_difference);
                                rectangle.setWidth(new_width);
                            } else {
                                rectangle.setX(collision_result[1] + 1);
                                rectangle.setWidth(rectangleChangedInfo.getOriginal_end_rectangle() - (collision_result[1] + 1));
                            }
                        } else if (rectangleChangedInfo.getOriginal_start_rectangle() - mouse_difference < time_line_pane_data.getTime_line_base_line()) {
                            if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), time_line_pane_data.getTime_line_base_line(), rectangleChangedInfo.getOriginal_end_rectangle())) {
                                rectangle.setX(time_line_pane_data.getTime_line_base_line());
                                rectangle.setWidth(rectangleChangedInfo.getOriginal_end_rectangle() - time_line_pane_data.getTime_line_base_line());
                            }
                        } else if (new_width < min_rectnagle_width) {
                            rectangle.setX(rectangleChangedInfo.getOriginal_end_rectangle() - min_rectnagle_width);
                            rectangle.setWidth(min_rectnagle_width);
                        }
                        double polygon_pos = return_polygon_middle_position(time_line_pane_data);
                        if (rectangle.getX() <= polygon_pos && rectangle.getX() + rectangle.getWidth() >= polygon_pos) {
                            set_the_chatgpt_image_view(helloController, rectangleChangedInfo.getImage_id(), Type_of_Image.THUMBNAIL_QUALITY);
                            rectangleChangedInfo.setDid_we_ever_change_the_photo(true);
                            create_and_attach_all_the_image_listeners(helloController, shapeObjectTimeLine);
                            enable_the_image_control_panel(helloController, shapeObjectTimeLine);
                        } else if (rectangleChangedInfo.isDid_we_ever_change_the_photo()) {
                            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.THUMBNAIL_QUALITY);
                            detach_all_the_image_control_listeners(helloController, shapeObjectTimeLine);
                            disable_the_image_control_panel(helloController);
                            reset_the_opacity(helloController);
                        }
                    } else if (rectangleChangedInfo.getType_of_movement() == MovementType.MIDDLE) {
                        if (mouse_scene_x_translated - local_x >= time_line_pane_data.getTime_line_base_line() && mouse_scene_x_translated - local_x + rectangle.getWidth() <= time_line_pane_data.getTime_line_end_base_line()) {
                            double[] collision_result = return_the_collision(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), mouse_scene_x_translated - local_x, mouse_scene_x_translated - local_x + rectangle.getWidth(), CollisionSearchType.Middle);
                            double x_pos = mouseEvent.getSceneX() - rectangleChangedInfo.getOriginal_x() + rectangleChangedInfo.getOriginal_start_rectangle();
                            if (collision_result[0] < 0) {
                                rectangle.setX(x_pos);
                                rectangleChangedInfo.getFake_rectangle().setVisible(false);
                            } else {
                                Rectangle fake_rectangle = rectangleChangedInfo.getFake_rectangle();
                                fake_rectangle.setVisible(true);
                                fake_rectangle.setX(x_pos);
                                if (rectangle.getX() < collision_result[0]) {
                                    if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), collision_result[0] - 1 - rectangle.getWidth(), collision_result[0] - 1)) {
                                        rectangle.setX(collision_result[0] - 1 - rectangle.getWidth());
                                    }
                                } else {
                                    if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), collision_result[1] + 1, collision_result[1] + 1 + rectangle.getWidth())) {
                                        rectangle.setX(collision_result[1] + 1);
                                    }
                                }
                            }
                        } else if (mouse_scene_x_translated - local_x < time_line_pane_data.getTime_line_base_line()) {
                            if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), time_line_pane_data.getTime_line_base_line(), time_line_pane_data.getTime_line_base_line() + rectangle.getWidth())) {
                                rectangle.setX(time_line_pane_data.getTime_line_base_line());
                            }
                            rectangleChangedInfo.getFake_rectangle().setX(time_line_pane_data.getTime_line_base_line());
                        } else if (mouse_scene_x_translated - local_x + rectangle.getWidth() > time_line_pane_data.getTime_line_end_base_line()) {
                            if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), time_line_pane_data.getTime_line_end_base_line() - rectangle.getWidth(), time_line_pane_data.getTime_line_base_line())) {
                                rectangle.setX(time_line_pane_data.getTime_line_end_base_line() - rectangle.getWidth());
                            }
                            rectangleChangedInfo.getFake_rectangle().setX(time_line_pane_data.getTime_line_end_base_line() - rectangle.getWidth());
                        }
                        double polygon_pos = return_polygon_middle_position(time_line_pane_data);
                        if (rectangle.getX() <= polygon_pos && rectangle.getX() + rectangle.getWidth() >= polygon_pos) {
                            set_the_chatgpt_image_view(helloController, rectangleChangedInfo.getImage_id(), Type_of_Image.THUMBNAIL_QUALITY);
                            rectangleChangedInfo.setDid_we_ever_change_the_photo(true);
                            create_and_attach_all_the_image_listeners(helloController, shapeObjectTimeLine);
                            enable_the_image_control_panel(helloController, shapeObjectTimeLine);
                        } else if (rectangleChangedInfo.isDid_we_ever_change_the_photo()) {
                            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.THUMBNAIL_QUALITY);
                            detach_all_the_image_control_listeners(helloController, shapeObjectTimeLine);
                            disable_the_image_control_panel(helloController);
                            reset_the_opacity(helloController);
                        }
                    } else if (rectangleChangedInfo.getType_of_movement() == MovementType.END) {
                        double original_width = rectangleChangedInfo.getOriginal_end_rectangle() - rectangleChangedInfo.getOriginal_start_rectangle();
                        double new_width = mouseEvent.getSceneX() - rectangleChangedInfo.getOriginal_x() + original_width;
                        double[] collision_result = return_the_collision(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), rectangleChangedInfo.getOriginal_start_rectangle(), rectangleChangedInfo.getOriginal_start_rectangle() + new_width, CollisionSearchType.End);
                        if (rectangle.getX() + new_width <= time_line_pane_data.getTime_line_end_base_line() && new_width >= min_rectnagle_width) {
                            if (collision_result[0] < 0) {
                                rectangle.setWidth(Math.min(new_width, max_rectangle_width));
                                /*if(new_width >= max_rectangle_width) {
                                    rectangle.setWidth(max_rectangle_width);
                                    double new_rectangle_start = rectangle.getX() + max_rectangle_width;
                                    double new_rectangle_end = new_rectangle_start + nanoseconds_to_pixels(time_line_pane_data,TimeUnit.MILLISECONDS.toNanos(1000));
                                    Media_pool media_pool = hashMap_with_media_pool_items.get(shapeObjectTimeLine.getImage_id());
                                    Rectangle created_rectangle = create_and_return_time_line_rectangle(helloController.time_line_pane,new_rectangle_start, nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(1000)));
                                    Shape_object_time_line new_rectangle_shape_object_timeLine = new Shape_object_time_line(new_rectangle_start, new_rectangle_end, created_rectangle, UUID.randomUUID().toString(), (long) nanoseconds_to_pixels(time_line_pane_data,(long) new_rectangle_start), (long) nanoseconds_to_pixels(time_line_pane_data,(long) (new_rectangle_end)), new Opacity_settings());
                                    set_up_the_image_rectangle(created_rectangle, media_pool.getThumbnail(), helloController.time_line_pane, new Opacity_settings());
                                    configure_the_image_rectangle(new_rectangle_shape_object_timeLine, helloController, helloController.time_line_pane, media_pool.getThumbnail());
                                    new_dragged_rectangle_created_already[0] = true;
                                } else {
                                    rectangle.setWidth(new_width);
                                }*/
                            } else {
                                double checked_rectangle_width = Math.min((collision_result[0] - 1) - rectangleChangedInfo.getOriginal_start_rectangle(), max_rectangle_width);
                                rectangle.setWidth(checked_rectangle_width);
                            }
                        } else if (new_width < min_rectnagle_width) {
                            rectangle.setWidth(min_rectnagle_width);
                        } else if (rectangle.getX() + new_width > time_line_pane_data.getTime_line_end_base_line()) {
                            if (!is_there_is_a_collosion(rectangleChangedInfo.getTree_set_containing_all_of_the_items(), rectangleChangedInfo.getOriginal_start_rectangle(), rectangleChangedInfo.getOriginal_start_rectangle() + time_line_pane_data.getTime_line_end_base_line() - rectangleChangedInfo.getOriginal_start_rectangle())) {
                                double checked_rectangle_width = Math.min(time_line_pane_data.getTime_line_end_base_line() - rectangleChangedInfo.getOriginal_start_rectangle(), max_rectangle_width);
                                rectangle.setWidth(checked_rectangle_width);
                            }
                        }
                        double polygon_pos = return_polygon_middle_position(time_line_pane_data);
                        if (rectangle.getX() <= polygon_pos && rectangle.getX() + rectangle.getWidth() >= polygon_pos) {
                            set_the_chatgpt_image_view(helloController, rectangleChangedInfo.getImage_id(), Type_of_Image.THUMBNAIL_QUALITY);
                            rectangleChangedInfo.setDid_we_ever_change_the_photo(true);
                            create_and_attach_all_the_image_listeners(helloController, shapeObjectTimeLine);
                            enable_the_image_control_panel(helloController, shapeObjectTimeLine);
                        } else if (rectangleChangedInfo.isDid_we_ever_change_the_photo()) {
                            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.THUMBNAIL_QUALITY);
                            detach_all_the_image_control_listeners(helloController, shapeObjectTimeLine);
                            disable_the_image_control_panel(helloController);
                            reset_the_opacity(helloController);
                        }
                    }
                }
            }
        });
        rectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (rectangle.getUserData() != null) {
                        Rectangle_changed_info rectangleChangedInfo = (Rectangle_changed_info) rectangle.getUserData();
                        double old_width = shapeObjectTimeLine.getEnd() - shapeObjectTimeLine.getStart();
                        if (rectangleChangedInfo.getType_of_movement() == MovementType.MIDDLE) { // added later
                            time_line_pane_data.getTree_set_containing_all_of_the_items().remove(shapeObjectTimeLine);
                        }
                        shapeObjectTimeLine.setStart(rectangle.getX());
                        shapeObjectTimeLine.setStart_time(pixels_to_nanoseconds(time_line_pane_data, rectangle.getX() - time_line_pane_data.getTime_line_base_line()));
                        shapeObjectTimeLine.setEnd(rectangle.getX() + rectangle.getWidth());
                        shapeObjectTimeLine.setEnd_time(pixels_to_nanoseconds(time_line_pane_data, rectangle.getX() + rectangle.getWidth() - time_line_pane_data.getTime_line_base_line()));
                        if (rectangleChangedInfo.getFake_rectangle() != null) {
                            pane.getChildren().remove(rectangleChangedInfo.getFake_rectangle());
                        }
                        check_if_i_am_in_right_rectangle_for_cursor(helloController, mouseEvent.getSceneX(), mouseEvent.getSceneY(), shapeObjectTimeLine, change_cursor_to_double_arrow_buffer, pane);
                        if (rectangle.getWidth() != old_width) {
                            set_up_the_image_rectangle(rectangle, image, pane, shapeObjectTimeLine.getOpacity_settings());
                        }
                        if (rectangleChangedInfo.getType_of_movement() == MovementType.MIDDLE) {// added becuase its removed earlier
                            time_line_pane_data.getTree_set_containing_all_of_the_items().add(shapeObjectTimeLine);
                        }
                        double polygon_x_pos = return_polygon_middle_position(time_line_pane_data);
                        if (polygon_x_pos >= shapeObjectTimeLine.getStart() && polygon_x_pos <= shapeObjectTimeLine.getEnd()) {
                            set_the_chatgpt_image_view(helloController, shapeObjectTimeLine.getImage_id(), Type_of_Image.FULL_QUALITY);
                        } else {
                            set_the_chatgpt_image_view(helloController, return_the_image_on_click(helloController.time_line_pane, polygon_x_pos), Type_of_Image.FULL_QUALITY);
                        }
                        rectangle.setUserData(null);
                    }
                }
                new_dragged_rectangle_created_already[0] = false;
            }
        });
    }

    private void disable_image_control_if_its_in_the_boundaries(HelloController helloController, Pane pane, Shape_object_time_line shape_object_time_line) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos = return_polygon_middle_position(time_line_pane_data);
        if (x_pos >= shape_object_time_line.getStart() && x_pos <= shape_object_time_line.getEnd()) {
            detach_all_the_image_control_listeners(helloController, shape_object_time_line);
            disable_the_image_control_panel(helloController);
        }
    }

    private void reset_the_opacity_if_its_not_in_boundaries(HelloController helloController, Pane pane, Shape_object_time_line shape_object_time_line) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos = return_polygon_middle_position(time_line_pane_data);
        if (x_pos >= shape_object_time_line.getStart() && x_pos <= shape_object_time_line.getEnd()) {
            reset_the_opacity(helloController);
        }
    }

    private void set_the_rectangle_mouse_cursor(HelloController helloController, double scene_x, double scene_y, Shape_object_time_line shapeObjectTimeLine, double change_cursor_to_double_arrow_buffer) {
        Rectangle rectangle = (Rectangle) shapeObjectTimeLine.getShape();
        double mouse_scene_x_translated = helloController.time_line_pane.sceneToLocal(scene_x, scene_y).getX();
        double local_x = mouse_scene_x_translated - shapeObjectTimeLine.getStart();
        if (mouse_scene_x_translated >= shapeObjectTimeLine.getStart() && mouse_scene_x_translated <= shapeObjectTimeLine.getEnd()) {
            if (get_type_of_movement(rectangle, local_x, change_cursor_to_double_arrow_buffer) == MovementType.START || get_type_of_movement(rectangle, local_x, change_cursor_to_double_arrow_buffer) == MovementType.END) {
                rectangle.setCursor(Cursor.H_RESIZE);
            } else {
                rectangle.setCursor(Cursor.OPEN_HAND);
            }
        }
    }

    private void check_if_i_am_in_right_rectangle_for_cursor(HelloController helloController, double scene_x, double scene_y, Shape_object_time_line shapeObjectTimeLine, double change_cursor_to_double_arrow_buffer, Pane pane) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double mouse_scene_x_translated = helloController.time_line_pane.sceneToLocal(scene_x, scene_y).getX();
        if (mouse_scene_x_translated >= shapeObjectTimeLine.getStart() && mouse_scene_x_translated <= shapeObjectTimeLine.getEnd()) {
            set_the_rectangle_mouse_cursor(helloController, scene_x, scene_y, shapeObjectTimeLine, change_cursor_to_double_arrow_buffer);
        } else {
            for (Shape_object_time_line shapeObjectTimeLine_from_tree_set : time_line_pane_data.getTree_set_containing_all_of_the_items()) {
                if (mouse_scene_x_translated >= shapeObjectTimeLine_from_tree_set.getStart() && mouse_scene_x_translated <= shapeObjectTimeLine_from_tree_set.getEnd()) {
                    set_the_rectangle_mouse_cursor(helloController, scene_x, scene_y, shapeObjectTimeLine_from_tree_set, change_cursor_to_double_arrow_buffer);
                    break;
                }
            }
        }
    }

    private MovementType get_type_of_movement(Rectangle rectangle, double local_x, double change_cursor_to_double_arrow_buffer) {
        if (rectangle.getWidth() >= change_cursor_to_double_arrow_buffer * 3) {
            if (local_x <= change_cursor_to_double_arrow_buffer) {
                return MovementType.START;
            } else if (rectangle.getWidth() - local_x <= change_cursor_to_double_arrow_buffer) {
                return MovementType.END;
            } else {
                return MovementType.MIDDLE;
            }
        } else {
            double local_change_cursor_to_double_arrow_buffer_start = rectangle.getWidth() / 3;
            double local_change_cursor_to_double_arrow_buffer_end = local_change_cursor_to_double_arrow_buffer_start;
            if (rectangle.getWidth() % 3 == 1) {
                local_change_cursor_to_double_arrow_buffer_start++;
            }
            if (rectangle.getWidth() % 3 == 2) {
                local_change_cursor_to_double_arrow_buffer_end++;
            }
            if (local_x <= local_change_cursor_to_double_arrow_buffer_start) {
                return MovementType.START;
            } else if (rectangle.getWidth() - local_x < local_change_cursor_to_double_arrow_buffer_end) {
                return MovementType.END;
            } else {
                return MovementType.MIDDLE;
            }
        }
    }

    private void set_the_opacity_of_the_rectangle_in_time_line_pane(Rectangle rectangle, double opacity) {
        rectangle.setOpacity(opacity);
    }

    /*private boolean is_there_is_a_collosion(double[][] sorted_array, double start, double end) {
        for (int i = 0; i < sorted_array.length; i++) {
            double local_start = sorted_array[i][0];
            double local_end = sorted_array[i][1];
            if (start > local_end) {
                continue;
            }
            if (local_start > end) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }*/

    private boolean is_there_is_a_collosion(TreeSet<Shape_object_time_line> tree_set_with_all_of_the_items, double start, double end) {
        Shape_object_time_line start_ceiled_shape_object_time_line = tree_set_with_all_of_the_items.floor(new Shape_object_time_line(start));
        Shape_object_time_line end_ceiled_shape_object_time_line = tree_set_with_all_of_the_items.floor(new Shape_object_time_line(end));
        if (start_ceiled_shape_object_time_line == null || end_ceiled_shape_object_time_line == null) {
            if (end_ceiled_shape_object_time_line == null) {
                return false;
            } else {
                return true;
            }
        } else {
            if (!start_ceiled_shape_object_time_line.equals(end_ceiled_shape_object_time_line)) {
                return true;
            } else {
                if (start_ceiled_shape_object_time_line.getEnd() < start) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    /*private double[] return_the_collision(double[][] sorted_array, double start, double end, CollisionSearchType collisionSearchType) {
        if (collisionSearchType == CollisionSearchType.End) {
            for (int i = 0; i < sorted_array.length; i++) {
                double local_start = sorted_array[i][0];
                double local_end = sorted_array[i][1];
                if (start > local_end) {
                    continue;
                }
                if (local_start > end) {
                    return new double[]{-1, -1};
                } else {
                    return new double[]{local_start, local_end};
                }
            }
            return new double[]{-1, -1};
        } else {
            for (int i = sorted_array.length - 1; i >= 0; i--) {
                double local_start = sorted_array[i][0];
                double local_end = sorted_array[i][1];
                if (end < local_start) {
                    continue;
                }
                if (local_end < start) {
                    return new double[]{-1, -1};
                } else {
                    return new double[]{local_start, local_end};
                }
            }
            return new double[]{-1, -1};
        }
    }*/

    private double[] return_the_collision(TreeSet<Shape_object_time_line> tree_set_containing_all_of_the_info, double start, double end, CollisionSearchType collisionSearchType) {
        Shape_object_time_line start_floored_shape_object_time_line = tree_set_containing_all_of_the_info.floor(new Shape_object_time_line(start));
        Shape_object_time_line end_floored_shape_object_time_line = tree_set_containing_all_of_the_info.floor(new Shape_object_time_line(end));
        if (start_floored_shape_object_time_line == null || end_floored_shape_object_time_line == null) {
            if (end_floored_shape_object_time_line == null) {
                return new double[]{-1, -1};
            } else {
                Shape_object_time_line shapeObjectTimeLine_ceiled_start = tree_set_containing_all_of_the_info.ceiling(new Shape_object_time_line(start));
                if (shapeObjectTimeLine_ceiled_start != null) {
                    return new double[]{shapeObjectTimeLine_ceiled_start.getStart(), shapeObjectTimeLine_ceiled_start.getEnd()};
                } else {
                    return new double[]{-1, -1};
                }
            }
        } else {
            if (!start_floored_shape_object_time_line.equals(end_floored_shape_object_time_line)) {
                if (collisionSearchType == CollisionSearchType.Start || collisionSearchType == CollisionSearchType.Middle) {
                    return new double[]{end_floored_shape_object_time_line.getStart(), end_floored_shape_object_time_line.getEnd()};
                } else {
                    return new double[]{end_floored_shape_object_time_line.getStart(), end_floored_shape_object_time_line.getEnd()};
                }
            } else {
                if (start_floored_shape_object_time_line.getEnd() < start) {
                    return new double[]{-1, -1};
                } else {
                    return new double[]{end_floored_shape_object_time_line.getStart(), end_floored_shape_object_time_line.getEnd()};
                }
            }
        }
    }

    private void black_out_the_image_view_at_the_start(HelloController helloController) {
        set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
    }

    /*private void listen_to_chatgpt_image_view_on_mouse_enetered_and_left(HelloController helloController) {
        helloController.stack_pane_of_image_view_and_text.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                helloController.blurry_chatgpt_image_view.setVisible(true);
                helloController.play_sound.setVisible(true);
            }
        });
        helloController.stack_pane_of_image_view_and_text.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                helloController.blurry_chatgpt_image_view.setVisible(false);
                helloController.play_sound.setVisible(false);
            }
        });
    }*/

    private void play_the_sound_on_chatgpt_image_clicked(HelloController helloController) {
        helloController.chatgpt_image_view.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                play_or_pause_the_video_after_click(helloController);
            }
        });
    }

    /*private void set_up_glossy_pause_button(HelloController helloController) {
        //GaussianBlur blur = new GaussianBlur(30); // Adjust the radius for more/less blur
        BoxBlur blur = new BoxBlur(60, 60, 3);
        helloController.blurry_chatgpt_image_view.setEffect(blur);
        Circle circle = new Circle(blurry_circle_raduis);
        helloController.blurry_chatgpt_image_view.setClip(circle);
    }*/

    /*private void make_the_blurry_chatgpt_image_always_have_the_same_size_as_non_blurry(HelloController helloController) {
        helloController.blurry_chatgpt_image_view.fitWidthProperty().bind(helloController.chatgpt_image_view.fitWidthProperty());
        helloController.blurry_chatgpt_image_view.fitHeightProperty().bind(helloController.chatgpt_image_view.fitHeightProperty());
    }*/

    private void set_the_chatgpt_image_view(HelloController helloController, String image_id, Type_of_Image typeOfImage) {
        if (!id_of_the_last_image.getImage_id().equals(image_id) || (id_of_the_last_image.getTypeOfImage() == Type_of_Image.THUMBNAIL_QUALITY && typeOfImage == Type_of_Image.FULL_QUALITY)) {
            id_of_the_last_image = new Last_shown_Image(image_id, typeOfImage);
            if (image_id.equals(no_image_found)) {
                helloController.chatgpt_image_view.setImage(blacked_out_image);
                //helloController.blurry_chatgpt_image_view.setImage(blacked_out_image_whitened);
            } else {
                Path base_path = get_os_base_path();
                Media_pool media_pool = hashMap_with_media_pool_items.get(image_id);
                helloController.chatgpt_image_view.setImage(media_pool.getBlacked_thumbnail());
                // helloController.blurry_chatgpt_image_view.setImage(whiten_the_image(image_to_buffered_image(media_pool.getBlacked_thumbnail()), 0.7f, 60f));
                if (typeOfImage == Type_of_Image.FULL_QUALITY) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (media_pool.isDid_the_image_get_down_scaled()) {
                                Path path = Paths.get(base_path.resolve("temp/images/scaled").toString(), image_id.concat(".raw"));
                                Image image = readRawImage(path.toString(), 1080, 1920);
                                helloController.chatgpt_image_view.setImage(image);
                            } else {
                                Path path = Paths.get(base_path.resolve("temp/images/base").toString(), image_id.concat(".raw"));
                                Image image = readRawImage(path.toString(), media_pool.getWidth(), media_pool.getHeight());
                                helloController.chatgpt_image_view.setImage(image);
                            }
                        }
                    });
                }
            }
        }
    }

    /*private void listen_to_blurry_image_view_size_change_and_center_the_clip(HelloController helloController) {
        double vertical_bias = 0.875D;
        Circle circle_from_glossy_pane = (Circle) helloController.blurry_chatgpt_image_view.getClip();
        circle_from_glossy_pane.centerXProperty().bind(new DoubleBinding() {
            {
                super.bind(helloController.blurry_chatgpt_image_view.fitWidthProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.blurry_chatgpt_image_view.getFitWidth() / 2;
            }
        });
        circle_from_glossy_pane.centerYProperty().bind(new DoubleBinding() {
            {
                super.bind(helloController.blurry_chatgpt_image_view.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                if ((helloController.blurry_chatgpt_image_view.getFitHeight() * vertical_bias) + blurry_circle_raduis > helloController.blurry_chatgpt_image_view.getFitHeight()) {
                    return ((helloController.blurry_chatgpt_image_view.getFitHeight() - blurry_circle_raduis * 2) * vertical_bias) + blurry_circle_raduis;
                } else {
                    return helloController.blurry_chatgpt_image_view.getFitHeight() * vertical_bias;
                }
            }
        });
        *//*helloController.blurry_chatgpt_image_view.fitWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (helloController.blurry_chatgpt_image_view.getClip() != null) {
                    ((Circle) helloController.blurry_chatgpt_image_view.getClip()).setCenterX(t1.doubleValue() / 2);
                    helloController.play_sound.setLayoutX(t1.doubleValue() / 2 - blurry_circle_raduis);
                }
            }
        });
        helloController.blurry_chatgpt_image_view.fitHeightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (helloController.blurry_chatgpt_image_view.getClip() != null) {
                    double vertical_bias = 0.875D;
                    ((Circle) helloController.blurry_chatgpt_image_view.getClip()).setCenterY(t1.doubleValue() * vertical_bias);
                    helloController.play_sound.setLayoutY(t1.doubleValue() * vertical_bias - blurry_circle_raduis);
                }
            }
        });*//*
    }*/

    /*private void bind_play_button_to_circle(HelloController helloController) {
        Circle circle_from_glossy_pane = (Circle) helloController.blurry_chatgpt_image_view.getClip();
        helloController.play_sound.layoutXProperty().bind(circle_from_glossy_pane.centerXProperty().subtract(blurry_circle_raduis));
        helloController.play_sound.layoutYProperty().bind(circle_from_glossy_pane.centerYProperty().subtract(blurry_circle_raduis));
    }*/

    /*private void make_play_button_circle(HelloController helloController) {
        helloController.play_sound.setPrefHeight(blurry_circle_raduis * 2);
        helloController.play_sound.setPrefWidth(blurry_circle_raduis * 2);
        helloController.play_sound.setShape(new Circle(blurry_circle_raduis));
    }*/

    private Rectangle deepCopyRectangle(Rectangle rectangle) {
        Rectangle copy = new Rectangle(
                rectangle.getX(),
                rectangle.getY(),
                rectangle.getWidth(),
                rectangle.getHeight()
        );
        copy.setFill(rectangle.getFill());
        copy.setStroke(rectangle.getStroke());
        copy.setStrokeWidth(rectangle.getStrokeWidth());
        copy.setArcWidth(rectangle.getArcWidth());
        copy.setArcHeight(rectangle.getArcHeight());
        copy.setOpacity(rectangle.getOpacity());
        copy.setRotate(rectangle.getRotate());
        copy.setEffect(rectangle.getEffect());
        return copy;
    }

    private Image return_16_9_black_image() {
        int width = 9;
        int height = 16;
        WritableImage writableImage = new WritableImage(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writableImage.getPixelWriter().setColor(i, j, new javafx.scene.paint.Color(0, 0, 0, 1));
            }
        }
        return writableImage;
    }

    private BufferedImage return_four_k_blacked_out_image() {
        BufferedImage buffered_image = new BufferedImage(Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = buffered_image.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height);
        graphics2D.dispose();
        return buffered_image;
    }


    private Image readRawImage(String filePath, int width, int height) {
        File file = new File(filePath);
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int read = fis.read(bytes);
            if (read != bytes.length) {
                throw new IOException("Could not read full file");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        /*int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixels[idx++];
                //int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = (argb) & 0xff;
                //writer.setColor(x, y, javafx.scene.paint.Color.rgb(r, g, b, a / 255.0));
                writer.setColor(x, y, javafx.scene.paint.Color.rgb(r, g, b));
            }
        }*/
        //writer.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        writer.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), bytes, 0, width * 3);

        return image;
    }

    private BufferedImage return_argb_buffered_image(BufferedImage bufferedImage) {
        if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            return bufferedImage;
        } else {
            BufferedImage new_rgb_buffered_image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            new_rgb_buffered_image.createGraphics().drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            return new_rgb_buffered_image;
        }
    }

    private Image whiten_the_image(BufferedImage image, float scale, float offset) {
        // Scale factors and offsets
        RescaleOp op = new RescaleOp(
                new float[]{scale, scale, scale, 1.0f},
                new float[]{offset, offset, offset, 0f}, null);

        BufferedImage result = op.filter(image, null);
        return buffer_image_to_image(result);
    }

    private String return_the_image_on_click(Pane pane, double x_position) {
        Shape_object_time_line shapeObjectTimeLine = return_the_shape_on_click(pane, x_position);
        if (shapeObjectTimeLine == null) {
            return no_image_found;
        } else {
            return shapeObjectTimeLine.getImage_id();
        }
    }

    private Shape_object_time_line return_the_shape_on_click(Pane pane, double x_position) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        TreeSet<Shape_object_time_line> treeSet = time_line_pane_data.getTree_set_containing_all_of_the_items();
        x_position = return_min_max_or_normal_x_postion(time_line_pane_data, x_position);
        Shape_object_time_line fake_shapeObjectTimeLine = new Shape_object_time_line(x_position);
        Shape_object_time_line floored_item = treeSet.floor(fake_shapeObjectTimeLine);
        if (floored_item == null || x_position > floored_item.getEnd()) {
            return null;
        } else {
            return floored_item;
        }
    }

    private Shape_object_time_line return_the_current_shape(Pane pane) {
        Time_line_pane_data timeLinePaneData = (Time_line_pane_data) pane.getUserData();
        double polygon_x_pos = return_polygon_middle_position(timeLinePaneData);
        Shape_object_time_line returned_shape_object_time_line = return_the_shape_on_click(pane, polygon_x_pos);
        if (returned_shape_object_time_line != null) {
            Rectangle shape_inside_shape_object_time_line = (Rectangle) returned_shape_object_time_line.getShape();
            if (polygon_x_pos >= shape_inside_shape_object_time_line.getLayoutX() && polygon_x_pos <= shape_inside_shape_object_time_line.getLayoutX() + shape_inside_shape_object_time_line.getWidth()) {
                return returned_shape_object_time_line;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String return_the_image_from_time(Pane pane, long nano_seconds) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos = nanoseconds_to_pixels(time_line_pane_data, nano_seconds) + time_line_pane_data.getTime_line_base_line();
        return return_the_image_on_click(pane, x_pos);
    }

    private double return_min_max_or_normal_x_postion(Time_line_pane_data time_line_pane_data, double x_pos) {
        if (x_pos < time_line_pane_data.getTime_line_base_line()) {
            return time_line_pane_data.getTime_line_base_line();
        } else if (x_pos > time_line_pane_data.getTime_line_end_base_line()) {
            return time_line_pane_data.getTime_line_end_base_line();
        } else {
            return x_pos;
        }
    }

    private double return_polygon_middle_position(Time_line_pane_data time_line_pane_data) {
        Polygon_data polygon_data = (Polygon_data) time_line_pane_data.getPolygon().getUserData();
        double start = polygon_data.getReal_polygon_position();
        return start + polygon_data.getPolygon_width() / 2;
    }

    private void clear_image_view_if_recatngle_is_in_boundries(HelloController helloController, Pane pane, Shape_object_time_line shapeObjectTimeLine) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos = return_polygon_middle_position(time_line_pane_data);
        if (x_pos >= shapeObjectTimeLine.getStart() && x_pos <= shapeObjectTimeLine.getEnd()) {
            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
        }
    }

    private void write_the_black_image_and_the_whitened_black_image() {
        blacked_out_image = return_16_9_black_image();
        blacked_out_image_whitened = whiten_the_image(image_to_buffered_image(blacked_out_image), 0.7f, 60f);
        blacked_out_image_four_k = return_four_k_blacked_out_image();
    }

    private ImageView return_the_icon(String name, int width, int height) {
        Image image = return_the_image_to_be_the_icon(name, width, height);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    private Image return_the_image_to_be_the_icon(String name, int width, int height) {
        String imagePath = "/icons/" + name + ".png";
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream != null) {
            /*ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;*/
            return new Image(inputStream);
        } else {
            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixelWriter.setArgb(x, y, 0x00000000); // Fully transparent, no visible color
                }
            }
            /*ImageView imageView = new ImageView(writableImage);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;*/
            return writableImage;
        }
    }

    private void get_the_quran_api_token(HelloController helloController, boolean call_apis, Scene scene) {
        String url = "";
        String authHeader;
        if (live_or_pre_live_quran_api.equals(Live_mode.PRE_LIVE)) {
            url = "https://prelive-oauth2.quran.foundation";
            authHeader = Credentials.basic(clientId_pre_live, clientSecret_pre_live, StandardCharsets.UTF_8);
        } else {
            url = "https://oauth2.quran.foundation";
            authHeader = Credentials.basic(clientId_live, clientSecret_live, StandardCharsets.UTF_8);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Time to establish connection
                .readTimeout(60, TimeUnit.SECONDS)    // Time to wait for data
                .writeTimeout(60, TimeUnit.SECONDS)   // Time allowed to send data
                .build();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("scope", "content")
                .build();
        Request request = new Request.Builder()
                .url(url + "/oauth2/token")
                .header("Authorization", authHeader)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected HTTP code: " + response.code()
                                + " - " + (response.body() != null ? response.body().string() : ""));
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(response.body().string());
                    quran_token = jsonNode.get("access_token").asText();
                    //long token_expiry = TimeUnit.SECONDS.toMillis(jsonNode.get("expires_in").asInt()) + System.currentTimeMillis();
                    refresh_the_quran_token(helloController, scene, jsonNode.get("expires_in").asInt());
                    if (call_apis) {
                        call_the_2_apis_at_the_start(helloController, scene);
                    }
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            show_alert("An error has occurred on the server side. Please try again later.");
                        }
                    });
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void set_the_logo_at_the_start(HelloController helloController) {
        Image image = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/Sabrly_mini.png")));
        helloController.logo_at_the_start_of_the_app.setImage(image);
    }

    private void make_the_first_real_screen_visible(HelloController helloController) {
        helloController.show_logo_loading_screen.setVisible(false);
        helloController.choose_surat_screen.setVisible(true);
    }

    private void center_the_progress_indicator(HelloController helloController, Scene scene) {
        if (helloController.show_logo_loading_screen.isVisible()) {
            double scene_height;
            if (scene.getHeight() == 0) {
                scene_height = Screen.getPrimary().getBounds().getHeight() * screen_height_multiplier;
            } else {
                scene_height = scene.getHeight();
            }
            double logo_height = helloController.logo_at_the_start_of_the_app.getFitHeight();
            double progress_spinner_height = helloController.progress_indicator_first_loading_screen.getPrefHeight();
            double distance_mid_to_bottom = scene_height / 2D;
            StackPane.setMargin(helloController.progress_indicator_first_loading_screen, new Insets(distance_mid_to_bottom - (progress_spinner_height / 2) + (logo_height / 2), 0, 0, 0));
        }
    }

    private void center_the_rendering_progress_indicator(HelloController helloController, Scene scene) {
        if (helloController.show_logo_loading_screen.isVisible()) {
            double scene_height;
            if (scene.getHeight() == 0) {
                scene_height = Screen.getPrimary().getBounds().getHeight() * screen_height_multiplier;
            } else {
                scene_height = scene.getHeight();
            }
            double logo_height = helloController.logo_at_the_start_of_the_app.getFitHeight();
            double rendering_progress_bar_height = helloController.vbox_holding_rendering_bar_and_label_render_engine_progress.getPrefHeight();
            double distance_mid_to_bottom = scene_height / 2D;
            StackPane.setMargin(helloController.vbox_holding_rendering_bar_and_label_render_engine_progress, new Insets(distance_mid_to_bottom - (rendering_progress_bar_height / 2) + (logo_height / 2), 0, 0, 0));
        }
    }

    private void listen_to_height_change_property(HelloController helloController, Scene scene) {
        heightListener_to_scene_for_logo_at_start = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                center_the_progress_indicator(helloController, scene);
                center_the_rendering_progress_indicator(helloController, scene);
            }
        };
        scene.heightProperty().addListener(heightListener_to_scene_for_logo_at_start);
    }

    private Request call_translations_api() {
        String baseUrl;
        String clientId;
        if (live_or_pre_live_quran_api.equals(Live_mode.LIVE)) {
            baseUrl = "https://apis.quran.foundation";
            clientId = clientId_live;
        } else {
            baseUrl = "https://apis-prelive.quran.foundation";
            clientId = clientId_pre_live;
        }
        HttpUrl url = HttpUrl.parse(baseUrl + "/content/api/v4/resources/translations")
                .newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("x-auth-token", quran_token)
                .addHeader("x-client-id", clientId)
                .build();
        return request;
        /*try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errBody = (response.body() != null ? response.body().string() : "");
                throw new IOException("Unexpected HTTP code: " + response.code() + " - " + errBody);
            }
            String translations_json = (response.body() != null ? response.body().string() : "");
            response.close();
            process_the_translations(translations_json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    private void process_the_translations(String response) {
        try {
            hash_map_with_the_translations = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            if (root != null && root.get("translations") != null) {
                JsonNode translations = root.get("translations");
                for (JsonNode item : translations) {
                    String language_name = item.get("language_name").asText();
                    int id = item.get("id").asInt();
                    ArrayList<Integer> arrayList_with_ids = hash_map_with_the_translations.getOrDefault(language_name, new ArrayList<>());
                    arrayList_with_ids.add(id);
                    hash_map_with_the_translations.put(language_name.toLowerCase(), arrayList_with_ids);
                    hashMap_id_to_language_name_text.put(id, language_name.toLowerCase());
                }
            } else {
                show_alert("There was a problem getting the translations. Please try again later.");
                throw new Exception("'translations' field is missing or null in JSON data.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void set_up_sound_for_chosen_verses(int start_ayat, int end_ayat) {
        long total_duration = getDurationWithFFmpeg(new File(sound_path));
        int total_ayats = end_ayat - start_ayat + 1;
        long duration_per_verse = total_duration / total_ayats;
        for (int i = 0; i < total_ayats; i++) {
            Verse_class_final verseClassFinal = new Verse_class_final(i + 1, duration_per_verse, duration_per_verse * i);
            ayats_processed.add(verseClassFinal);
        }
    }

    private String return_the_translation_string() {
        StringBuilder string_with_all_of_the_translations_to_be_returned = new StringBuilder();
        for (String key : hash_map_with_the_translations.keySet()) {
            ArrayList<Integer> array_list_with_ids = hash_map_with_the_translations.get(key);
            if (array_list_with_ids.size() == 1) {
                string_with_all_of_the_translations_to_be_returned.append(hash_map_with_the_translations.get(key).get(0));
                string_with_all_of_the_translations_to_be_returned.append(",");
            } else {
                int id = -1;
                if (key.equals("albanian")) {
                    id = 89;
                } else if (key.equals("azeri")) {
                    id = 75;
                } else if (key.equals("bambara")) {
                    id = 796;
                } else if (key.equals("bengali")) {
                    id = 213;
                } else if (key.equals("bosnian")) {
                    id = 126;
                } else if (key.equals("chinese")) {
                    id = 109;
                } else if (key.equals("divehi")) {
                    id = 840;
                } else if (key.equals("english")) {
                    id = 85;
                } else if (key.equals("french")) {
                    id = 136;
                } else if (key.equals("german")) {
                    id = 27;
                } else if (key.equals("hausa")) {
                    id = 32;
                } else if (key.equals("indonesian")) {
                    id = 33;
                } else if (key.equals("italian")) {
                    id = 153;
                } else if (key.equals("japanese")) {
                    id = 218;
                } else if (key.equals("kazakh")) {
                    id = 222;
                } else if (key.equals("korean")) {
                    id = 219;
                } else if (key.equals("kurdish")) {
                    id = 81;
                } else if (key.equals("malayalam")) {
                    id = 37;
                } else if (key.equals("persian")) {
                    id = 29;
                } else if (key.equals("portuguese")) {
                    id = 43;
                } else if (key.equals("romanian")) {
                    id = 782;
                } else if (key.equals("russian")) {
                    id = 45;
                } else if (key.equals("somali")) {
                    id = 786;
                } else if (key.equals("spanish")) {
                    id = 83;
                } else if (key.equals("tajik")) {
                    id = 74;
                } else if (key.equals("tamil")) {
                    id = 133;
                } else if (key.equals("thai")) {
                    id = 51;
                } else if (key.equals("turkish")) {
                    id = 77;
                } else if (key.equals("urdu")) {
                    id = 234;
                } else if (key.equals("uzbek")) {
                    id = 55;
                } else if (key.equals("vietnamese")) {
                    id = 221;
                }
                if (id == -1 || !array_list_with_ids.contains(id)) {
                    System.err.println("There has been an error getting the correct translation. A substitute translation has been chosen");
                    int min_id = Collections.min(hash_map_with_the_translations.get(key));
                    string_with_all_of_the_translations_to_be_returned.append(min_id);
                    string_with_all_of_the_translations_to_be_returned.append(",");
                } else {
                    string_with_all_of_the_translations_to_be_returned.append(id).append(",");
                }
            }
        }
        if (!string_with_all_of_the_translations_to_be_returned.isEmpty()) {
            string_with_all_of_the_translations_to_be_returned.deleteCharAt(string_with_all_of_the_translations_to_be_returned.length() - 1);
        }
        return string_with_all_of_the_translations_to_be_returned.toString();
    }

    private CompletableFuture<Response> callAsync(OkHttpClient client, Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                future.complete(response);
            }
        });
        return future;
    }

    private void call_the_2_apis_at_the_start(HelloController helloController, Scene scene) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Time to establish connection
                .readTimeout(10, TimeUnit.SECONDS)    // Time to wait for data
                .writeTimeout(10, TimeUnit.SECONDS)   // Time allowed to send data
                .build();
        Request translation_request = call_translations_api();
        Request chapters_request = call_chapters_api(helloController, scene);
        CompletableFuture<String> translationsFuture = callAsync(client, translation_request)
                .thenApply(new Function<Response, String>() {
                    @Override
                    public String apply(Response response) {
                        try {
                            if (response.body() != null) {
                                return response.body().string();
                            } else {
                                return "";
                            }
                        } catch (IOException e) {

                            throw new RuntimeException(e);
                        }
                    }
                });

        CompletableFuture<String> chaptersFuture =
                callAsync(client, chapters_request)
                        .thenApply(new Function<Response, String>() {
                            @Override
                            public String apply(Response response) {
                                try {
                                    if (response.body() != null) {
                                        return response.body().string();
                                    } else {
                                        return "";
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
        CompletableFuture
                .allOf(translationsFuture, chaptersFuture)
                .thenRun(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String translationsJson = translationsFuture.get();
                            chapters_string = chaptersFuture.get();
                            // If these touch UI, do it on FX thread:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    add_surats_to_the_list_view(helloController, chapters_string);
                                    process_the_translations(translationsJson);
                                    everything_to_be_called_at_the_start(helloController, scene);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                System.err.println("API failed: " + throwable.getMessage());
                                show_alert("Could not form a connection with the API, please try restarting the program. If the error persists, please try again later.");
                                throw new RuntimeException("API call failed", throwable); // this will propagate
                            }
                        });
                        return null;
                    }
                });
    }

    private void scroll_pane_time_line_h_vlaue_listener(HelloController helloController) {
        helloController.scroll_pane_hosting_the_time_line.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                set_the_canvas(helloController, t1.doubleValue(), helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth());
                Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                if (time_line_pane_data != null && !is_the_polygon_locked_in(helloController)) {
                    Polygon_data polygon_data = (Polygon_data) get_the_polygon_from_the_time_line_over_lay(helloController).getUserData();
                    draw_the_polygon_time_line(helloController, polygon_data.getReal_polygon_position());
                }
            }
        });
    }

    private void set_the_canvas(HelloController helloController, double h_value, double canvas_width) {
        final double pixels_in_between_each_line = 10;
        final double long_line_length = 20;
        final double half_long_line_length = 13;
        final double line_length = 7.5;
        final double line_thickness = 1.5;
        final int start_lines_offset = 9;
        final long time_between_every_line = TimeUnit.MILLISECONDS.toNanos(50);
        final javafx.scene.paint.Color long_line_color = javafx.scene.paint.Color.rgb(100, 101, 103);
        final javafx.scene.paint.Color mid_line_color = javafx.scene.paint.Color.rgb(89, 95, 103);
        final javafx.scene.paint.Color short_line_color = javafx.scene.paint.Color.rgb(66, 71, 78);
        final javafx.scene.paint.Color time_text_color = javafx.scene.paint.Color.rgb(146, 146, 146);
        final javafx.scene.paint.Color time_line_indicitor_color = javafx.scene.paint.Color.rgb(206, 47, 40);
        Canvas canvas = helloController.canvas_on_top_of_time_line_pane;
        double pixel_position = return_pixel_position(helloController.scroll_pane_hosting_the_time_line.getContent().getLayoutBounds().getWidth(), canvas_width, h_value);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int base_number_of_divider = (int) Math.floorDiv((long) pixel_position, (long) pixels_in_between_each_line) + start_lines_offset;
        double first_offset = pixel_position % pixels_in_between_each_line;
        double visible_number_of_dividers = canvas_width / pixels_in_between_each_line + 1;
        for (int i = 0; i < visible_number_of_dividers; i++) {
            int current_number_of_divider = base_number_of_divider + i;
            double x_pos = (i * pixels_in_between_each_line) - first_offset;
            if ((time_between_every_line * current_number_of_divider) % TimeUnit.MILLISECONDS.toNanos(1000) == 0) {
                draw_the_line_graphics_context(graphicsContext, x_pos, line_thickness, long_line_length, long_line_color);
                add_the_text_graphics_context(graphicsContext, time_between_every_line * (current_number_of_divider - start_lines_offset), x_pos, line_length, time_text_color); // TODO allow drawing of text in engative direction
            } else if ((time_between_every_line * current_number_of_divider) % TimeUnit.MILLISECONDS.toNanos(500) == 0) {
                draw_the_line_graphics_context(graphicsContext, x_pos, line_thickness, half_long_line_length, mid_line_color);
            } else {
                draw_the_line_graphics_context(graphicsContext, x_pos, line_thickness, line_length, short_line_color);
            }
        }
    }

    private void draw_the_line_graphics_context(GraphicsContext graphicsContext, double x_pos, double line_width, double line_length, javafx.scene.paint.Color line_color) {
        graphicsContext.setStroke(line_color);       // same as setStroke(...) on Line
        graphicsContext.setLineWidth(line_width);    // same as setStrokeWidth(...)
        graphicsContext.strokeLine(x_pos, 0, x_pos, line_length);
    }

    private void add_the_text_graphics_context(GraphicsContext graphicsContext, long millisecond, double line_end, double line_length, javafx.scene.paint.Color color) {
        Font font = new Font(11);
        Text time_label = new Text(convertnanosecondsToTime(millisecond));
        time_label.setFont(font);
        Bounds bounds = time_label.getLayoutBounds();
        graphicsContext.setFont(font);
        graphicsContext.setFill(color);
        graphicsContext.fillText(time_label.getText(), line_end + 5, line_length - bounds.getMinY() + 2.5);
    }

    private void bind_scroll_pane_height_to_canvas(HelloController helloController) {
        helloController.canvas_on_top_of_time_line_pane.heightProperty().bind(helloController.scroll_pane_hosting_the_time_line.heightProperty().subtract(scroll_pane_hosting_time_line_border_width * 2));
        helloController.canvas_on_top_of_time_line_pane.widthProperty().bind(helloController.scroll_pane_hosting_the_time_line.widthProperty().subtract(scroll_pane_hosting_time_line_border_width * 2));
    }

    private void listen_to_canvas_size_change(HelloController helloController) {
        helloController.canvas_on_top_of_time_line_pane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                set_the_canvas(helloController, helloController.scroll_pane_hosting_the_time_line.getHvalue(), t1.doubleValue());
            }
        });
    }

    private void set_the_width_of_the_border_of_scroll_pane(HelloController helloController) {
        String new_style = helloController.scroll_pane_hosting_the_time_line.getStyle() + "-fx-border-width: " + String.valueOf(scroll_pane_hosting_time_line_border_width);
        helloController.scroll_pane_hosting_the_time_line.setStyle(new_style);
    }

    private void set_the_time_line_over_laying_pane_margin(HelloController helloController) {
        StackPane.setMargin(helloController.pane_overlying_the_time_line_pane_for_polygon_indicator, new Insets(scroll_pane_hosting_time_line_border_width, 0, 0, 0));
    }

    private void bind_scroll_pane_view_port_to_pane_over_laying_time_line(HelloController helloController) {
        DoubleBinding width_binding = new DoubleBinding() {
            {
                super.bind(helloController.scroll_pane_hosting_the_time_line.viewportBoundsProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
            }
        };

        DoubleBinding height_binding = new DoubleBinding() {
            {
                super.bind(helloController.scroll_pane_hosting_the_time_line.viewportBoundsProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getHeight();
            }
        };
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.minWidthProperty().bind(width_binding);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.prefWidthProperty().bind(width_binding);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.maxWidthProperty().bind(width_binding);

        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.minHeightProperty().bind(height_binding);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.prefHeightProperty().bind(height_binding);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.maxHeightProperty().bind(height_binding);
    }

    private Polygon set_up_the_polygon_for_the_overlaying_pane(HelloController helloController, double start_x, javafx.scene.paint.Color color, Pane pane) {
        final double full_length = 12.5D;
        final double partial_length = 7.5D;
        final double rectangle_width = 2.5D;
        final double time_line_indicator_width = 15D;
        final double right_side_of_rectangle = ((time_line_indicator_width - rectangle_width) / 2) + rectangle_width;
        final double left_side_of_rectangle = (time_line_indicator_width - rectangle_width) / 2;
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        Polygon_data polygon_data;
        double height = pane.getHeight();
        Polygon polygon = new Polygon();
        polygon.setSmooth(true);
        polygon.getPoints().addAll(
                0D, 0D,     // Top-left
                time_line_indicator_width, 0D,
                time_line_indicator_width, partial_length,
                right_side_of_rectangle, full_length,
                right_side_of_rectangle, height,
                left_side_of_rectangle, height,
                left_side_of_rectangle, full_length,
                0D, partial_length
        );
        polygon.setFill(color);
        polygon.setLayoutX(start_x - (time_line_indicator_width / 2));
        polygon.setLayoutY(0);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().addFirst(polygon);
        polygon_data = new Polygon_data(start_x - (time_line_indicator_width / 2), time_line_indicator_width);
        /*time_line_pane_data.setReal_polygon_position(start_x - (time_line_indicator_width / 2));
        time_line_pane_data.setPolygon_width(time_line_indicator_width);*/
        polygon.setUserData(polygon_data);
        return polygon;
    }

    private void clip_the_over_the_over_laying_pane(HelloController helloController) {
        DoubleBinding width_binding = new DoubleBinding() {
            {
                super.bind(helloController.scroll_pane_hosting_the_time_line.viewportBoundsProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
            }
        };

        DoubleBinding height_binding = new DoubleBinding() {
            {
                super.bind(helloController.scroll_pane_hosting_the_time_line.viewportBoundsProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getHeight();
            }
        };
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(width_binding);
        clip.heightProperty().bind(height_binding);
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.setClip(clip);
    }

    private double return_pixel_position(double contnet_width, double view_port_width, double h_value) {
        return (contnet_width - view_port_width) * h_value;
    }

    private void draw_the_polygon_time_line(HelloController helloController, double contnet_width, double view_port_width, double h_value, double x_pos) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        double start_of_window = return_pixel_position(contnet_width, view_port_width, h_value);
        double end_of_window = start_of_window + view_port_width;
        if (x_pos >= start_of_window && x_pos <= end_of_window) {
            double real_position = x_pos - start_of_window;
            polygon.setLayoutX(real_position);
        }
    }

    private void draw_the_polygon_time_line(HelloController helloController, double x_pos) {
        double contnet_width = helloController.scroll_pane_hosting_the_time_line.getContent().getLayoutBounds().getWidth();
        double view_port_width = helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
        double h_value = helloController.scroll_pane_hosting_the_time_line.getHvalue();
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        double polygon_width = polygon_data.getPolygon_width();
        double start_of_window = return_pixel_position(contnet_width, view_port_width, h_value);
        double end_of_window = start_of_window + view_port_width;
        double start_window_minus_width = start_of_window - polygon_width;
        double end_of_window_plus_width = end_of_window + polygon_width;
        if (x_pos >= start_window_minus_width && x_pos <= end_of_window_plus_width) {
            double real_position = x_pos - start_of_window;
            polygon.setLayoutX(real_position);
            polygon.setVisible(true);
        } else {
            polygon.setVisible(false);
        }
    }

    private void set_the_polygon_real_start(Polygon polygon, double x_pos) {
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        polygon_data.setReal_polygon_position(x_pos);
    }

    private void listen_to_pane_over_time_line_being_resized(HelloController helloController) {
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.prefWidthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
                Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
                draw_the_polygon_time_line(helloController, helloController.scroll_pane_hosting_the_time_line.getContent().getLayoutBounds().getWidth(), t1.doubleValue(), helloController.scroll_pane_hosting_the_time_line.getHvalue(), polygon_data.getReal_polygon_position());
            }
        });
    }

    private Polygon get_the_polygon_from_the_time_line_over_lay(HelloController helloController) {
        /*if(helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().isEmpty()){
            return new Polygon();
        }
        if(helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().get(0) instanceof Polygon){
            return (Polygon) helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().get(0);
        } else {
            for(int i = 0;i<helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().size();i++){
                if(helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().get(i) instanceof Polygon){
                    return (Polygon) helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getChildren().get(i);
                }
            }
        }
        return new Polygon();*/
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        return time_line_pane_data.getPolygon();
    }

    private void set_the_polygon_to_the_middle_when_video_is_playing(HelloController helloController) {
        double view_port_width = helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.getWidth();
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        double half_polygon = polygon_data.getPolygon_width() / 2;
        polygon.setLayoutX((view_port_width / 2) - half_polygon);
        polygon.setVisible(true);
    }

    private void set_the_status_of_locked_in_polygon(HelloController helloController, boolean locked_in) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        if (polygon_data != null) {
            polygon_data.setShould_the_polygon_be_fixed_in_the_middle(locked_in);
        }
    }

    private boolean is_the_polygon_locked_in(HelloController helloController) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        if (polygon_data == null || !polygon_data.isShould_the_polygon_be_fixed_in_the_middle()) {
            return false;
        } else {
            return true;
        }
    }

    private Polygon_mode_time_line return_the_polygon_status_according_to_the_time_line(double x_position, double half_polygon_width, double half_visible_width, double contentWidth) {
        if (x_position + half_polygon_width - half_visible_width <= 0) {
            return Polygon_mode_time_line.BEFORE_MIDDLE;
        } else if (x_position + half_polygon_width + half_visible_width >= contentWidth) {
            return Polygon_mode_time_line.AFTER_MIDDLE;
        } else {
            return Polygon_mode_time_line.MIDDLE;
        }
    }

    private void ignore_scroll_for_overlaying_pane_for_time_line(HelloController helloController) {
        helloController.pane_overlying_the_time_line_pane_for_polygon_indicator.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                // Try to forward to the ScrollPane's viewport (preferred)
                Node target = helloController.scroll_pane_hosting_the_time_line.getContent();
                target.fireEvent(event.copyFor(target, target));
                event.consume(); // prevent double handling
            }
        });
    }

    private void refresh_the_quran_token(HelloController helloController, Scene scene, int time_in_seconds) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                get_the_quran_api_token(helloController, false, scene);
                scheduler.shutdown(); // shutdown after execution
            }
        }, time_in_seconds - number_of_seconds_of_quran_token_pre_fire, TimeUnit.SECONDS);
    }

    private HashMap<String, Integer> get_the_language_ranking() {
        HashMap<String, Integer> hash_map_with_each_language_ranking = new HashMap<>();
        hash_map_with_each_language_ranking.put("arabic", 0);
        hash_map_with_each_language_ranking.put("english", 1);
        hash_map_with_each_language_ranking.put("indonesian", 2);
        hash_map_with_each_language_ranking.put("urdu", 3);
        hash_map_with_each_language_ranking.put("bengali", 4);
        hash_map_with_each_language_ranking.put("turkish", 5);
        hash_map_with_each_language_ranking.put("french", 6);
        hash_map_with_each_language_ranking.put("hausa", 7);
        hash_map_with_each_language_ranking.put("persian", 8);
        hash_map_with_each_language_ranking.put("spanish", 9);
        hash_map_with_each_language_ranking.put("swahili", 10);
        hash_map_with_each_language_ranking.put("malay", 11);
        hash_map_with_each_language_ranking.put("hindi", 12);
        hash_map_with_each_language_ranking.put("russian", 13);
        hash_map_with_each_language_ranking.put("pashto", 14);
        hash_map_with_each_language_ranking.put("somali", 15);
        hash_map_with_each_language_ranking.put("uzbek", 16);
        hash_map_with_each_language_ranking.put("dari", 17);
        hash_map_with_each_language_ranking.put("kazakh", 18);
        hash_map_with_each_language_ranking.put("albanian", 19);
        hash_map_with_each_language_ranking.put("azeri", 20);
        hash_map_with_each_language_ranking.put("kurdish", 21);
        hash_map_with_each_language_ranking.put("oromo", 22);
        hash_map_with_each_language_ranking.put("amharic", 23);
        hash_map_with_each_language_ranking.put("portuguese", 24);
        hash_map_with_each_language_ranking.put("yoruba", 25);
        hash_map_with_each_language_ranking.put("bosnian", 26);
        hash_map_with_each_language_ranking.put("tajik", 27);
        hash_map_with_each_language_ranking.put("italian", 28);
        hash_map_with_each_language_ranking.put("german", 29);
        hash_map_with_each_language_ranking.put("japanese", 30);
        hash_map_with_each_language_ranking.put("korean", 31);
        hash_map_with_each_language_ranking.put("vietnamese", 32);
        hash_map_with_each_language_ranking.put("thai", 33);
        hash_map_with_each_language_ranking.put("chinese", 34);
        hash_map_with_each_language_ranking.put("romanian", 35);
        hash_map_with_each_language_ranking.put("polish", 36);
        hash_map_with_each_language_ranking.put("czech", 37);
        hash_map_with_each_language_ranking.put("swedish", 38);
        hash_map_with_each_language_ranking.put("dutch", 39);
        hash_map_with_each_language_ranking.put("tamil", 40);
        hash_map_with_each_language_ranking.put("malayalam", 41);
        hash_map_with_each_language_ranking.put("telugu", 42);
        hash_map_with_each_language_ranking.put("kannada", 43);
        hash_map_with_each_language_ranking.put("marathi", 44);
        hash_map_with_each_language_ranking.put("gujarati", 45);
        hash_map_with_each_language_ranking.put("sindhi", 46);
        hash_map_with_each_language_ranking.put("assamese", 47);
        hash_map_with_each_language_ranking.put("uighur, uyghur", 48);
        hash_map_with_each_language_ranking.put("tatar", 49);
        hash_map_with_each_language_ranking.put("chechen", 50);
        hash_map_with_each_language_ranking.put("serbian", 51);
        hash_map_with_each_language_ranking.put("macedonian", 52);
        hash_map_with_each_language_ranking.put("bulgarian", 53);
        hash_map_with_each_language_ranking.put("ukrainian", 54);
        hash_map_with_each_language_ranking.put("nepali", 55);
        hash_map_with_each_language_ranking.put("central khmer", 56);
        hash_map_with_each_language_ranking.put("kinyarwanda", 57);
        hash_map_with_each_language_ranking.put("ganda", 58);
        hash_map_with_each_language_ranking.put("bambara", 59);
        hash_map_with_each_language_ranking.put("amazigh", 60);
        hash_map_with_each_language_ranking.put("tagalog", 61);
        hash_map_with_each_language_ranking.put("norwegian", 62);
        hash_map_with_each_language_ranking.put("finnish", 63);
        hash_map_with_each_language_ranking.put("hebrew", 64);
        hash_map_with_each_language_ranking.put("divehi", 65);
        hash_map_with_each_language_ranking.put("maranao", 66);
        hash_map_with_each_language_ranking.put("yau,yuw", 67);
        hash_map_with_each_language_ranking.put("sinhala, sinhalese", 68);
        return hash_map_with_each_language_ranking;
    }

    private void set_up_the_languages(HelloController helloController, HashMap<String, ArrayList<String>> hashMap_with_all_of_the_translations_of_verses) {
        ObservableList<Language_info> items = FXCollections.observableArrayList();
        for (String key : hash_map_with_the_translations.keySet()) {
            if (!key.equals("english") && !key.equals("arabic")) {
                items.add(new Language_info(key, return_the_formatted_text_item_from_array_list(hashMap_with_all_of_the_translations_of_verses.get(key))));
            }
        }
        //HashMap<String, Integer> ranking_languages_hash_map = get_the_language_ranking();
        items.sort(new Comparator<Language_info>() {
            @Override
            public int compare(Language_info o1, Language_info o2) {
                /*int language_one_value = ranking_languages_hash_map.get(o1.getLanguage_name());
                int language_two_value = ranking_languages_hash_map.get(o2.getLanguage_name());
                return Integer.compare(language_one_value, language_two_value);*/
                return o1.getLanguage_name().compareTo(o2.getLanguage_name());
            }
        });
        items.add(0, new Language_info("arabic", return_the_formatted_text_item_from_array_list(hashMap_with_all_of_the_translations_of_verses.get("arabic"))));
        items.add(1, new Language_info("english", return_the_formatted_text_item_from_array_list(hashMap_with_all_of_the_translations_of_verses.get("english"))));
        helloController.list_view_with_all_of_the_languages.setItems(items);
        helloController.list_view_with_all_of_the_languages.setCellFactory(new javafx.util.Callback<ListView<Language_info>, ListCell<Language_info>>() {
            @Override
            public ListCell<Language_info> call(ListView<Language_info> listView) {
                return new ListCell<Language_info>() {
                    private VBox root;
                    private StackPane stackPane_of_the_top;
                    private JFXButton jfxButton;
                    private Label language_name;
                    private ImageView down_or_left_image_view;
                    private StackPane stackPane_extended_with_all_of_the_info;
                    private VBox v_box_inside_the_stack_pane;
                    private CheckBox check_box_is_the_langauge_enabled;
                    private Separator separator_between_check_box_and_everything;
                    private HBox hbox_for_x_and_y_positions;
                    private TextField x_position_of_text;
                    private TextField y_position_of_text;
                    private Label x_label_beside_x_pos;
                    private Label y_label_beside_y_pos;
                    private VBox v_box_with_all_of_the_controls_except_check_box;
                    private ColorPicker color_picker;
                    private HBox hbox_containing_the_font_size;
                    private Label label_beside_the_font_size;
                    private TextField text_field_for_font_size;
                    private Region item_at_the_bottom_of_extended_pane;
                    private Label text_saying_position_above_position;
                    private HBox hbox_hosting_the_position_label;
                    private Label label_saying_color;
                    private HBox hbox_containing_the_text_color_label;
                    private Separator separator_under_position;
                    private Separator separator_under_color_picker;
                    private ComboBox<String> combox_of_all_of_fonts;
                    private Label label_saying_fonts;
                    private HBox hbox_hosting_the_fonts_label;
                    private Separator separator_under_font_picker;
                    private ComboBox<Sub_font_name_and_style> combox_of_all_of_fonts_sub_choices;
                    private JFXButton reset_prefrence_button;
                    private JFXButton increase_font_size_button;
                    private JFXButton decrease_font_size_button;
                    private HBox hbox_for_plus_and_minus;
                    private Separator separator_under_stroke;
                    private Label label_saying_stroke;
                    private HBox hbox_hosting_the_stroke_label;
                    private CheckBox stroke_check_box;
                    private Region region_in_the_middle_of_stuff_horizontal;
                    private VBox vbox_carrying_the_stroke_stuff;
                    private ColorPicker stroke_color_picker;
                    private Slider stroke_weight_slider;
                    private Label label_saying_wieght_beside_slider;
                    private HBox hbox_hosting_the_weight_label_and_the_slider;
                    private Separator separator_under_advanced_options;
                    private JFXButton reset_everything_button;
                    private Separator separator_between_reset_and_apply_to_all_button;
                    private JFXButton apply_to_all_verses_button;
                    private HBox hbox_hosting_reset_and_apply_to_all;
                    private Label label_hosting_the_percentage_of_weight;
                    private Label fake_label_for_stroke_weight_space;
                    private StackPane stack_pane_holding_the_stroke_weights;
                    private Separator separator_under_font_size;
                    private HBox hbox_holding_the_margin_label;
                    private Label margin_label;
                    private HBox hbox_hosting_the_left_margin;
                    private Label label_saying_left_margin;
                    private TextField left_margin_input_field;
                    private HBox hbox_containing_the_plus_minus_for_left_margin;
                    private JFXButton increase_left_margin_button;
                    private JFXButton decrease_left_margin_button;
                    private HBox hbox_hosting_the_right_margin;
                    private Label label_saying_right_margin;
                    private TextField right_margin_input_field;
                    private HBox hbox_containing_the_plus_minus_for_right_margin;
                    private JFXButton increase_right_margin_button;
                    private JFXButton decrease_right_margin_button;
                    private Label fake_left_margin_label;
                    private Label fake_right_margin_label;
                    private StackPane pane_holding_left_margin_label;
                    private StackPane pane_holding_right_margin_label;
                    private HBox hbox_holding_the_advanced_options_toggle;
                    private Label label_holding_advanced_options;
                    private ToggleSwitch toggle_switch_for_advanced_options;
                    private VBox holds_advnaced_options;
                    private JFXButton center_button_x_pos;
                    private JFXButton center_button_y_pos;
                    private Separator separator_inside_advanced_options_before_everything;
                    private Label shadow_label;
                    private HBox hbox_holding_the_shadow_label;
                    private Slider shadow_slider;
                    private Label label_holding_the_shadow_value;
                    private Label fake_label_for_shadow_value;
                    private Label label_saying_weight_before_shadow_slider;
                    private HBox hbox_holding_the_shadow_slider_and_value;
                    private CheckBox shadow_check_box;
                    private ColorPicker shadow_color_picker;
                    private VBox vbox_holding_everything_shadow;
                    private Region region_to_expand_space_for_shadow_check_box;
                    private StackPane stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight;
                    private Label fade_label_at_the_top;
                    private HBox hbox_holding_the_fade_label;
                    private Label fade_in_label_for_verse;
                    private Label fake_fade_in_label_for_verse;
                    private StackPane stack_pane_holding_fade_in_and_fake_fade_in_for_verse;
                    private Slider fade_in_slider_for_verse;
                    private Label label_holding_fade_in_time_for_verse;
                    private Label fake_label_holding_fade_in_time_for_verse;
                    private StackPane stack_pane_holding_real_and_fake_fade_in_time_for_verse;
                    private Label fade_out_label_for_verse;
                    private Label fake_fade_out_label_for_verse;
                    private StackPane stack_pane_holding_fade_out_and_fake_fade_out_for_verse;
                    private Slider fade_out_slider_for_verse;
                    private Label label_holding_fade_out_time_for_verse;
                    private Label fake_label_holding_fade_out_time_for_verse;
                    private StackPane stack_pane_holding_real_and_fake_fade_out_time_for_verse;
                    private Separator separator_under_fade_in_fade_out_verse;
                    private HBox hbox_holding_verse_fade_in_and_slider;
                    private HBox hbox_holding_verse_fade_out_and_slider;
                    private Label label_saying_verse;
                    private HBox hbox_holding_the_verse_label;
                    private TextArea verse_text_area;
                    private JFXButton reset_text_area_and_verse_to_original_verse_button;
                    private Separator separator_under_shadow;

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        root = new VBox(0);
                        stackPane_of_the_top = new StackPane();
                        jfxButton = new JFXButton();
                        language_name = new Label();
                        check_box_is_the_langauge_enabled = new CheckBox();
                        stackPane_extended_with_all_of_the_info = new StackPane();
                        v_box_inside_the_stack_pane = new VBox();
                        separator_between_check_box_and_everything = new Separator();
                        hbox_for_x_and_y_positions = new HBox();
                        x_position_of_text = new TextField();
                        y_position_of_text = new TextField();
                        x_label_beside_x_pos = new Label();
                        y_label_beside_y_pos = new Label();
                        v_box_with_all_of_the_controls_except_check_box = new VBox();
                        color_picker = new ColorPicker(javafx.scene.paint.Color.WHITE);
                        hbox_containing_the_font_size = new HBox();
                        label_beside_the_font_size = new Label();
                        text_field_for_font_size = new TextField();
                        item_at_the_bottom_of_extended_pane = new Region();
                        text_saying_position_above_position = new Label();
                        hbox_hosting_the_position_label = new HBox();
                        label_saying_color = new Label();
                        hbox_containing_the_text_color_label = new HBox();
                        separator_under_position = new Separator();
                        separator_under_color_picker = new Separator();
                        combox_of_all_of_fonts = new ComboBox<>();
                        label_saying_fonts = new Label();
                        hbox_hosting_the_fonts_label = new HBox();
                        separator_under_font_picker = new Separator();
                        combox_of_all_of_fonts_sub_choices = new ComboBox<>();
                        reset_prefrence_button = new JFXButton();
                        increase_font_size_button = new JFXButton();
                        decrease_font_size_button = new JFXButton();
                        hbox_for_plus_and_minus = new HBox();
                        separator_under_stroke = new Separator();
                        label_saying_stroke = new Label();
                        hbox_hosting_the_stroke_label = new HBox();
                        stroke_check_box = new CheckBox();
                        region_in_the_middle_of_stuff_horizontal = new Region();
                        vbox_carrying_the_stroke_stuff = new VBox();
                        stroke_color_picker = new ColorPicker();
                        stroke_weight_slider = new Slider();
                        label_saying_wieght_beside_slider = new Label();
                        hbox_hosting_the_weight_label_and_the_slider = new HBox();
                        separator_under_advanced_options = new Separator();
                        reset_everything_button = new JFXButton();
                        separator_between_reset_and_apply_to_all_button = new Separator();
                        apply_to_all_verses_button = new JFXButton();
                        hbox_hosting_reset_and_apply_to_all = new HBox();
                        label_hosting_the_percentage_of_weight = new Label();
                        fake_label_for_stroke_weight_space = new Label();
                        stack_pane_holding_the_stroke_weights = new StackPane();
                        separator_under_font_size = new Separator();
                        hbox_holding_the_margin_label = new HBox();
                        margin_label = new Label();
                        hbox_hosting_the_left_margin = new HBox();
                        label_saying_left_margin = new Label();
                        left_margin_input_field = new TextField();
                        hbox_containing_the_plus_minus_for_left_margin = new HBox();
                        increase_left_margin_button = new JFXButton();
                        decrease_left_margin_button = new JFXButton();
                        hbox_hosting_the_right_margin = new HBox();
                        label_saying_right_margin = new Label();
                        right_margin_input_field = new TextField();
                        hbox_containing_the_plus_minus_for_right_margin = new HBox();
                        increase_right_margin_button = new JFXButton();
                        decrease_right_margin_button = new JFXButton();
                        fake_left_margin_label = new Label();
                        fake_right_margin_label = new Label();
                        pane_holding_left_margin_label = new StackPane();
                        pane_holding_right_margin_label = new StackPane();
                        hbox_holding_the_advanced_options_toggle = new HBox();
                        label_holding_advanced_options = new Label();
                        toggle_switch_for_advanced_options = new ToggleSwitch();
                        holds_advnaced_options = new VBox();
                        center_button_x_pos = new JFXButton();
                        center_button_y_pos = new JFXButton();
                        separator_inside_advanced_options_before_everything = new Separator();
                        shadow_label = new Label();
                        hbox_holding_the_shadow_label = new HBox();
                        shadow_slider = new Slider();
                        label_holding_the_shadow_value = new Label();
                        hbox_holding_the_shadow_slider_and_value = new HBox();
                        label_saying_weight_before_shadow_slider = new Label();
                        shadow_check_box = new CheckBox();
                        shadow_color_picker = new ColorPicker();
                        vbox_holding_everything_shadow = new VBox();
                        region_to_expand_space_for_shadow_check_box = new Region();
                        fake_label_for_shadow_value = new Label();
                        stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight = new StackPane();
                        fade_label_at_the_top = new Label();
                        hbox_holding_the_fade_label = new HBox();
                        fade_in_label_for_verse = new Label();
                        fake_fade_in_label_for_verse = new Label();
                        stack_pane_holding_fade_in_and_fake_fade_in_for_verse = new StackPane();
                        fade_in_slider_for_verse = new Slider();
                        label_holding_fade_in_time_for_verse = new Label();
                        fake_label_holding_fade_in_time_for_verse = new Label();
                        stack_pane_holding_real_and_fake_fade_in_time_for_verse = new StackPane();
                        fade_out_label_for_verse = new Label();
                        fake_fade_out_label_for_verse = new Label();
                        stack_pane_holding_fade_out_and_fake_fade_out_for_verse = new StackPane();
                        fade_out_slider_for_verse = new Slider();
                        label_holding_fade_out_time_for_verse = new Label();
                        fake_label_holding_fade_out_time_for_verse = new Label();
                        stack_pane_holding_real_and_fake_fade_out_time_for_verse = new StackPane();
                        separator_under_fade_in_fade_out_verse = new Separator();
                        hbox_holding_verse_fade_in_and_slider = new HBox();
                        hbox_holding_verse_fade_out_and_slider = new HBox();
                        label_saying_verse = new Label();
                        hbox_holding_the_verse_label = new HBox();
                        verse_text_area = new TextArea();
                        reset_text_area_and_verse_to_original_verse_button = new JFXButton();
                        separator_under_shadow = new Separator();


                        final double top_margin_in_vbox_control = 10;
                        final double half_top_margin_in_vbox_control = 5;
                        final double three_quarters_margin_in_vbox_control = 7.5;
                        final double start_and_end_margin = 8.5;
                        final double separator_start_end = 5;
                        final double min_stroke_weight = 0;
                        final double max_stroke_weight = Global_default_values.max_stroke_weight;
                        final double min_shadow_weight = 0;
                        final double max_shadow_weight = Global_default_values.max_shadow_weight;
                        final double buttons_at_the_bottom_height = 32.5;
                        final double left_margin_at_the_start_of_stroke_shadow_weight = 5;

                        //root
                        bind_the_root_to_list_view(helloController, root, paddingProperty());
                        //stackPane_of_the_top
                        set_pref_min_max(stackPane_of_the_top, 40, Resize_bind_type.HEIGHT);

                        //jfxButton
                        bind_an_item_to_a_property(jfxButton, root.widthProperty(), 0);
                        set_pref_min_max(jfxButton, 40, Resize_bind_type.HEIGHT);
                        jfxButton.setFocusTraversable(false);

                        //lnaguage_name
                        language_name.setMouseTransparent(true);
                        StackPane.setMargin(language_name, new Insets(0, 0, 0, 5));
                        StackPane.setAlignment(language_name, Pos.CENTER_LEFT);

                        //check_box_is_the_langauge_enabled
                        check_box_is_the_langauge_enabled.setText("Show Text");

                        //stackPane_extended_with_all_of_the_info
                        //set_pref_min_max(stackPane_extended_with_all_of_the_info, 120, Resize_bind_type.HEIGHT);
                        stackPane_extended_with_all_of_the_info.setVisible(false);
                        stackPane_extended_with_all_of_the_info.setManaged(false);

                        //v_box_inside_the_stack_pane
                        v_box_inside_the_stack_pane.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(v_box_inside_the_stack_pane, Pos.BOTTOM_CENTER);
                        StackPane.setMargin(v_box_inside_the_stack_pane, new Insets(10, 0, 0, 0));

                        //separator_between_check_box_and_everything
                        VBox.setMargin(separator_between_check_box_and_everything, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));
                        //separator_between_check_box_and_everything.getStyleClass().add("my-separator");

                        //down_or_left_image_view
                        down_or_left_image_view = return_the_icon("arrow_forward_ios", width_and_height_of_arrow_image_view_translation, width_and_height_of_arrow_image_view_translation);
                        down_or_left_image_view.setMouseTransparent(true);
                        StackPane.setMargin(down_or_left_image_view, new Insets(0, 5, 0, 0));
                        StackPane.setAlignment(down_or_left_image_view, Pos.CENTER_RIGHT);

                        //hbox_for_x_and_y_positions
                        hbox_for_x_and_y_positions.setAlignment(Pos.CENTER);
                        VBox.setMargin(hbox_for_x_and_y_positions, new Insets(half_top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_for_x_and_y_positions, root.widthProperty(), start_and_end_margin * 2);

                        //x_position_of_text
                        format_the_text_filed_to_only_accept_positive_integers(x_position_of_text);
                        HBox.setMargin(x_position_of_text, new Insets(0, 0, 0, 5));
                        HBox.setHgrow(x_position_of_text, Priority.ALWAYS);
                        x_position_of_text.setMaxWidth(Double.MAX_VALUE);


                        //y_position_of_text
                        format_the_text_filed_to_only_accept_positive_integers(y_position_of_text);
                        HBox.setMargin(y_position_of_text, new Insets(0, 0, 0, 5));
                        HBox.setHgrow(y_position_of_text, Priority.ALWAYS);
                        y_position_of_text.setMaxWidth(Double.MAX_VALUE);

                        //x_label_beside_x_pos
                        x_label_beside_x_pos.setText("X:");
                        x_label_beside_x_pos.setMinWidth(Region.USE_PREF_SIZE);

                        //y_label_beside_y_pos
                        y_label_beside_y_pos.setText("Y:");
                        HBox.setMargin(y_label_beside_y_pos, new Insets(0, 0, 0, 10));
                        y_label_beside_y_pos.setMinWidth(Region.USE_PREF_SIZE);

                        //v_box_with_all_of_the_controls_except_check_box
                        v_box_with_all_of_the_controls_except_check_box.setDisable(true);
                        v_box_with_all_of_the_controls_except_check_box.setAlignment(Pos.CENTER);

                        //color_picker
                        VBox.setMargin(color_picker, new Insets(half_top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(color_picker, root.widthProperty(), start_and_end_margin * 2);
                        color_picker.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                color_picker.show();
                            }
                        });

                        //hbox_containing_the_font_size
                        hbox_containing_the_font_size.setAlignment(Pos.CENTER);
                        VBox.setMargin(hbox_containing_the_font_size, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_containing_the_font_size, root.widthProperty(), start_and_end_margin * 2);

                        //label_beside_the_font_size
                        label_beside_the_font_size.setText("Font Size:");

                        //text_field_for_font_size
                        format_the_text_filed_to_only_accept_positive_integers(text_field_for_font_size);
                        HBox.setMargin(text_field_for_font_size, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(text_field_for_font_size, Priority.ALWAYS);
                        text_field_for_font_size.setMaxWidth(Double.MAX_VALUE);

                        //item_at_the_bottom_of_extended_pane
                        item_at_the_bottom_of_extended_pane.setPrefSize(0, 0);
                        item_at_the_bottom_of_extended_pane.setMinSize(0, 0);
                        item_at_the_bottom_of_extended_pane.setMaxSize(0, 0);
                        item_at_the_bottom_of_extended_pane.setVisible(false);
                        item_at_the_bottom_of_extended_pane.setManaged(true);
                        VBox.setMargin(item_at_the_bottom_of_extended_pane, new Insets(5, 0, 0, 0));

                        //text_saying_position_above_position
                        text_saying_position_above_position.setText("Position");

                        //hbox_hosting_the_position_label
                        hbox_hosting_the_position_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_hosting_the_position_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //label_saying_color
                        label_saying_color.setText("Fill");

                        //hbox_containing_the_text_color_label
                        hbox_containing_the_text_color_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_containing_the_text_color_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //separator_under_position
                        VBox.setMargin(separator_under_position, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //separator_under_color_picker
                        VBox.setMargin(separator_under_color_picker, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //combox_of_all_of_fonts
                        add_the_fonts_to_the_combo_box(combox_of_all_of_fonts);
                        bind_an_item_to_a_property(combox_of_all_of_fonts, root.widthProperty(), start_and_end_margin * 2);
                        VBox.setMargin(combox_of_all_of_fonts, new Insets(half_top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        combox_of_all_of_fonts.getSelectionModel().select("System");

                        //label_saying_fonts
                        label_saying_fonts.setText("Typography");

                        //hbox_hosting_the_fonts_label
                        hbox_hosting_the_fonts_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_hosting_the_fonts_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //separator_under_font_picker
                        VBox.setMargin(separator_under_font_picker, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //combox_of_all_of_fonts_sub_choices
                        bind_an_item_to_a_property(combox_of_all_of_fonts_sub_choices, root.widthProperty(), start_and_end_margin * 2);
                        VBox.setMargin(combox_of_all_of_fonts_sub_choices, new Insets(half_top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        combox_of_all_of_fonts_sub_choices.setCellFactory(new javafx.util.Callback<ListView<Sub_font_name_and_style>, ListCell<Sub_font_name_and_style>>() {
                            @Override
                            public ListCell<Sub_font_name_and_style> call(ListView<Sub_font_name_and_style> fontNameAndStyleListView) {
                                return new ListCell<Sub_font_name_and_style>() {
                                    @Override
                                    protected void updateItem(Sub_font_name_and_style item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty || item == null) {
                                            setText(null);
                                        } else {
                                            setText(item.getFont_name());
                                        }
                                    }
                                };
                            }
                        });
                        combox_of_all_of_fonts_sub_choices.setButtonCell(new ListCell<Sub_font_name_and_style>() {
                            @Override
                            protected void updateItem(Sub_font_name_and_style item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    setText(item.getFont_name());
                                }
                            }
                        });
                        add_all_sub_fonts_to_sub_fonts_combo_box(combox_of_all_of_fonts, combox_of_all_of_fonts_sub_choices);

                        //increase_font_size_button
                        increase_font_size_button.setFocusTraversable(false);
                        increase_font_size_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        increase_font_size_button.prefHeightProperty().bind(text_field_for_font_size.heightProperty());
                        increase_font_size_button.minHeightProperty().bind(text_field_for_font_size.heightProperty());
                        increase_font_size_button.maxHeightProperty().bind(text_field_for_font_size.heightProperty());
                        increase_font_size_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                increase_font_size_button.setGraphic(return_the_icon("plus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                increase_font_size_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                increase_font_size_button.setAlignment(Pos.CENTER);
                            }
                        });

                        //decrease_font_size_button
                        decrease_font_size_button.setFocusTraversable(false);
                        decrease_font_size_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        decrease_font_size_button.prefHeightProperty().bind(text_field_for_font_size.heightProperty());
                        decrease_font_size_button.minHeightProperty().bind(text_field_for_font_size.heightProperty());
                        decrease_font_size_button.maxHeightProperty().bind(text_field_for_font_size.heightProperty());
                        decrease_font_size_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                decrease_font_size_button.setGraphic(return_the_icon("minus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                decrease_font_size_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                decrease_font_size_button.setAlignment(Pos.CENTER);
                            }
                        });
                        /*decrease_font_size_button.setShape(new Rectangle(25,25));
                        decrease_font_size_button.setGraphic(return_the_icon("minus", 25, 25));
                        decrease_font_size_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        decrease_font_size_button.setAlignment(Pos.CENTER);*/

                        //hbox_for_plus_and_minus
                        HBox.setMargin(hbox_for_plus_and_minus, new Insets(0, 0, 0, 10));
                        hbox_for_plus_and_minus.setStyle("-fx-border-color: #F3F3F3; -fx-border-width: 1;");

                        //separator_under_stroke
                        VBox.setMargin(separator_under_stroke, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //label_saying_stroke
                        label_saying_stroke.setText("Stroke");

                        //hbox_hosting_the_stroke_label
                        hbox_hosting_the_stroke_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_hosting_the_stroke_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //region_in_the_middle_of_stuff_horizontal
                        HBox.setHgrow(region_in_the_middle_of_stuff_horizontal, Priority.ALWAYS);

                        //stroke_check_box

                        //vbox_carrying_the_stroke_stuff
                        vbox_carrying_the_stroke_stuff.setDisable(true);


                        //stroke_color_picker
                        VBox.setMargin(stroke_color_picker, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(stroke_color_picker, root.widthProperty(), start_and_end_margin * 2);
                        stroke_color_picker.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                stroke_color_picker.show();
                            }
                        });
                        stroke_color_picker.setValue(javafx.scene.paint.Color.BLACK);

                        //label_saying_wieght_beside_slider
                        label_saying_wieght_beside_slider.setText("Weight: ");

                        //stroke_weight_slider
                        HBox.setMargin(stroke_weight_slider, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(stroke_weight_slider, Priority.ALWAYS);
                        stroke_weight_slider.setMaxWidth(Double.MAX_VALUE);
                        stroke_weight_slider.setMin(min_stroke_weight);
                        stroke_weight_slider.setMax(max_stroke_weight);
                        set_up_the_tick_marks_for_a_slider(stroke_weight_slider, 0.1);

                        //hbox_hosting_the_weight_label_and_the_slider
                        VBox.setMargin(hbox_hosting_the_weight_label_and_the_slider, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_hosting_the_weight_label_and_the_slider, root.widthProperty(), start_and_end_margin * 2);

                        //separator_under_advanced_options
                        VBox.setMargin(separator_under_advanced_options, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //hbox_hosting_reset_and_apply_to_all
                        VBox.setMargin(hbox_hosting_reset_and_apply_to_all, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_hosting_the_weight_label_and_the_slider, root.widthProperty(), start_and_end_margin * 2);

                        //reset_everything_button
                        reset_everything_button.setText("Reset");
                        HBox.setHgrow(reset_everything_button, Priority.ALWAYS);
                        reset_everything_button.setMaxWidth(Double.MAX_VALUE);
                        set_pref_min_max(reset_everything_button, (int) buttons_at_the_bottom_height, Resize_bind_type.HEIGHT);

                        //separator_between_reset_and_apply_to_all_button
                        HBox.setMargin(separator_between_reset_and_apply_to_all_button, new Insets(2.5, 0, 2.5, 10));
                        separator_between_reset_and_apply_to_all_button.setOrientation(Orientation.VERTICAL);

                        //apply_to_all_versees_button
                        apply_to_all_verses_button.setText("Apply to All");
                        HBox.setMargin(apply_to_all_verses_button, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(apply_to_all_verses_button, Priority.ALWAYS);
                        apply_to_all_verses_button.setMaxWidth(Double.MAX_VALUE);
                        set_pref_min_max(apply_to_all_verses_button, buttons_at_the_bottom_height, Resize_bind_type.HEIGHT);

                        //label_hosting_the_percentage_of_weight
                        //HBox.setMargin(label_hosting_the_percentage_of_weight, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));
                        //label_hosting_the_percentage_of_weight.setText(String.valueOf(return_percentage_based_on_value(Global_default_values.stroke_weight,stroke_weight_slider.getMax())) + "%");
                        label_hosting_the_percentage_of_weight.setText(return_formatted_string_to_1_decimal_place_always(Global_default_values.stroke_weight));

                        //fake_label_for_stroke_weight_space
                        //HBox.setMargin(fake_label_for_stroke_weight_space, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));
                        String widest_text = find_the_widest_text(0, stroke_weight_slider.getMax(), 0.1);
                        fake_label_for_stroke_weight_space.setVisible(false);
                        fake_label_for_stroke_weight_space.setText(widest_text);

                        //stack_pane_holding_the_stroke_weights
                        HBox.setMargin(stack_pane_holding_the_stroke_weights, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));

                        //separator_under_font_size
                        VBox.setMargin(separator_under_font_size, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //hbox_holding_the_margin_label
                        hbox_holding_the_margin_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_holding_the_margin_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //margin_label
                        margin_label.setText("Margin");

                        //hbox_hosting_the_left_margin
                        hbox_hosting_the_left_margin.setAlignment(Pos.CENTER);
                        VBox.setMargin(hbox_hosting_the_left_margin, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_hosting_the_left_margin, root.widthProperty(), start_and_end_margin * 2);

                        //label_saying_left_margin
                        label_saying_left_margin.setText("Left Margin: ");

                        //left_margin_input_field
                        format_the_text_filed_to_only_accept_positive_integers(left_margin_input_field);
                        HBox.setMargin(left_margin_input_field, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(left_margin_input_field, Priority.ALWAYS);
                        left_margin_input_field.setMaxWidth(Double.MAX_VALUE);
                        left_margin_input_field.setText("0");

                        //hbox_containing_the_plus_minus_for_left_margin
                        HBox.setMargin(hbox_containing_the_plus_minus_for_left_margin, new Insets(0, 0, 0, 10));
                        hbox_containing_the_plus_minus_for_left_margin.setStyle("-fx-border-color: #F3F3F3; -fx-border-width: 1;");

                        //increase_left_margin_button
                        increase_left_margin_button.setFocusTraversable(false);
                        increase_left_margin_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        increase_left_margin_button.prefHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_left_margin_button.minHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_left_margin_button.maxHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_left_margin_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                increase_left_margin_button.setGraphic(return_the_icon("plus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                increase_left_margin_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                increase_left_margin_button.setAlignment(Pos.CENTER);
                            }
                        });

                        //decrease_left_margin_button
                        decrease_left_margin_button.setFocusTraversable(false);
                        decrease_left_margin_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        decrease_left_margin_button.prefHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_left_margin_button.minHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_left_margin_button.maxHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_left_margin_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                decrease_left_margin_button.setGraphic(return_the_icon("minus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                decrease_left_margin_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                decrease_left_margin_button.setAlignment(Pos.CENTER);
                            }
                        });

                        //hbox_hosting_the_right_margin
                        hbox_hosting_the_right_margin.setAlignment(Pos.CENTER);
                        VBox.setMargin(hbox_hosting_the_right_margin, new Insets(half_top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(hbox_hosting_the_right_margin, root.widthProperty(), start_and_end_margin * 2);

                        //label_saying_right_margin
                        label_saying_right_margin.setText("Right Margin: ");

                        //right_margin_input_field
                        format_the_text_filed_to_only_accept_positive_integers(right_margin_input_field);
                        HBox.setMargin(right_margin_input_field, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(right_margin_input_field, Priority.ALWAYS);
                        right_margin_input_field.setMaxWidth(Double.MAX_VALUE);
                        right_margin_input_field.setText("0");

                        //hbox_containing_the_plus_minus_for_right_margin
                        HBox.setMargin(hbox_containing_the_plus_minus_for_right_margin, new Insets(0, 0, 0, 10));
                        hbox_containing_the_plus_minus_for_right_margin.setStyle("-fx-border-color: #F3F3F3; -fx-border-width: 1;");

                        //increase_right_margin_button
                        increase_right_margin_button.setFocusTraversable(false);
                        increase_right_margin_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        increase_right_margin_button.prefHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_right_margin_button.minHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_right_margin_button.maxHeightProperty().bind(left_margin_input_field.heightProperty());
                        increase_right_margin_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                increase_right_margin_button.setGraphic(return_the_icon("plus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                increase_right_margin_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                increase_right_margin_button.setAlignment(Pos.CENTER);
                            }
                        });

                        //decrease_right_margin_button
                        decrease_right_margin_button.setFocusTraversable(false);
                        decrease_right_margin_button.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        decrease_right_margin_button.prefHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_right_margin_button.minHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_right_margin_button.maxHeightProperty().bind(left_margin_input_field.heightProperty());
                        decrease_right_margin_button.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                decrease_right_margin_button.setGraphic(return_the_icon("minus", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                decrease_right_margin_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                decrease_right_margin_button.setAlignment(Pos.CENTER);
                            }
                        });

                        //fake_left_margin_label
                        fake_left_margin_label.setText("Left Margin: ");
                        fake_left_margin_label.setVisible(false);

                        //fake_right_margin_label
                        fake_right_margin_label.setText("Right Margin: ");
                        fake_right_margin_label.setVisible(false);

                        //pane_holding_left_margin_label
                        pane_holding_left_margin_label.setAlignment(Pos.CENTER);
                        pane_holding_left_margin_label.getChildren().add(label_saying_left_margin);
                        pane_holding_left_margin_label.getChildren().add(fake_right_margin_label);


                        //pane_holding_right_margin_label
                        pane_holding_left_margin_label.setAlignment(Pos.CENTER);
                        pane_holding_right_margin_label.getChildren().add(label_saying_right_margin);
                        pane_holding_right_margin_label.getChildren().add(fake_left_margin_label);

                        //hbox_holding_the_advanced_options_toggle
                        hbox_holding_the_advanced_options_toggle.setAlignment(Pos.CENTER);
                        VBox.setMargin(hbox_holding_the_advanced_options_toggle, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //label_holding_advanced_options
                        label_holding_advanced_options.setText("Advanced Options");

                        //toggle_switch_for_advanced_options
                        toggle_switch_for_advanced_options.getStyleClass().add("toggle_switch_style");

                        //holds_advnaced_options
                        holds_advnaced_options.setAlignment(Pos.CENTER);
                        holds_advnaced_options.setVisible(false);
                        holds_advnaced_options.setManaged(false);

                        //center_button_x_pos
                        HBox.setMargin(center_button_x_pos, new Insets(0, 0, 0, 2));
                        center_button_x_pos.setFocusTraversable(false);
                        center_button_x_pos.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        center_button_x_pos.minHeightProperty().bind(x_position_of_text.heightProperty());
                        center_button_x_pos.prefHeightProperty().bind(x_position_of_text.heightProperty());
                        center_button_x_pos.maxHeightProperty().bind(x_position_of_text.heightProperty());
                        /*center_button_x_pos.minWidthProperty().bind(x_position_of_text.heightProperty());
                        center_button_x_pos.prefWidthProperty().bind(x_position_of_text.heightProperty());
                        center_button_x_pos.maxWidthProperty().bind(x_position_of_text.heightProperty());*/
                        center_button_x_pos.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                center_button_x_pos.setGraphic(return_the_icon("center", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                center_button_x_pos.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                center_button_x_pos.setAlignment(Pos.CENTER);
                            }
                        });


                        //center_button_y_pos
                        HBox.setMargin(center_button_y_pos, new Insets(0, 0, 0, 2));
                        center_button_y_pos.setFocusTraversable(false);
                        center_button_y_pos.setRipplerFill(javafx.scene.paint.Color.web(hex_ripple_coulour_for_jfx_buttons));
                        center_button_y_pos.prefHeightProperty().bind(y_position_of_text.heightProperty());
                        center_button_y_pos.minHeightProperty().bind(y_position_of_text.heightProperty());
                        center_button_y_pos.maxHeightProperty().bind(y_position_of_text.heightProperty());
                        center_button_y_pos.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                center_button_y_pos.setGraphic(return_the_icon("center", (int) (t1.doubleValue() * 2D / 3D), (int) (t1.doubleValue() * 2D / 3D)));
                                center_button_y_pos.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                center_button_y_pos.setAlignment(Pos.CENTER);
                            }
                        });

                        //separator_inside_advanced_options_before_everything
                        VBox.setMargin(separator_inside_advanced_options_before_everything, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //shadow_label
                        shadow_label.setText("Shadow");

                        //hbox_holding_the_shadow_label
                        hbox_holding_the_shadow_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_holding_the_shadow_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //shadow_slider
                        HBox.setMargin(shadow_slider, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(shadow_slider, Priority.ALWAYS);
                        shadow_slider.setMaxWidth(Double.MAX_VALUE);
                        shadow_slider.setMin(min_shadow_weight);
                        shadow_slider.setMax(max_shadow_weight);
                        set_up_the_tick_marks_for_a_slider(shadow_slider, 0.1);
                        shadow_slider.setValue(Global_default_values.shadow_weight);

                        //label_holding_the_shadow_value
                        label_holding_the_shadow_value.setText(return_formatted_string_to_1_decimal_place_always(Global_default_values.shadow_weight));

                        //label_saying_weight_before_shadow_slider
                        label_saying_weight_before_shadow_slider.setText("Weight:");

                        //region_to_expand_space_for_shadow_check_box
                        HBox.setHgrow(region_to_expand_space_for_shadow_check_box, Priority.ALWAYS);

                        //shadow_color_picker
                        VBox.setMargin(shadow_color_picker, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        bind_an_item_to_a_property(shadow_color_picker, root.widthProperty(), start_and_end_margin * 2);
                        shadow_color_picker.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                shadow_color_picker.show();
                            }
                        });
                        shadow_color_picker.setValue(javafx.scene.paint.Color.BLACK);

                        //hbox_holding_the_shadow_slider_and_value
                        hbox_holding_the_shadow_slider_and_value.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_holding_the_shadow_slider_and_value, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //vbox_holding_everything_shadow
                        vbox_holding_everything_shadow.setDisable(true);

                        //fake_label_for_shadow_value
                        String widest_text_for_shadow = find_the_widest_text(0, shadow_slider.getMax(), 0.1);
                        fake_label_for_shadow_value.setVisible(false);
                        fake_label_for_shadow_value.setText(widest_text_for_shadow);

                        //stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight
                        HBox.setMargin(stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));

                        //fade_label_at_the_top
                        fade_label_at_the_top.setText("Fade");

                        //hbox_holding_the_fade_label
                        hbox_holding_the_fade_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_holding_the_fade_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //hbox_holding_verse_fade_in_and_slider
                        VBox.setMargin(hbox_holding_verse_fade_in_and_slider, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //fade_in_label_for_verse
                        fade_in_label_for_verse.setText("Fade in:");

                        //fake_fade_in_label_for_verse
                        fake_fade_in_label_for_verse.setText("Fade out:");
                        fake_fade_in_label_for_verse.setVisible(false);

                        //stack_pane_holding_fade_in_and_fake_fade_in_for_verse
                        stack_pane_holding_fade_in_and_fake_fade_in_for_verse.setAlignment(Pos.CENTER_LEFT);

                        //fade_in_slider_for_verse
                        HBox.setMargin(fade_in_slider_for_verse, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(fade_in_slider_for_verse, Priority.ALWAYS);
                        fade_in_slider_for_verse.setMaxWidth(Double.MAX_VALUE);
                        fade_in_slider_for_verse.setMin(0);
                        fade_in_slider_for_verse.setMax(2);
                        set_up_the_tick_marks_for_a_slider(fade_in_slider_for_verse, 0.1);

                        //label_holding_fade_in_time_for_verse
                        label_holding_fade_in_time_for_verse.setText("0.0s");

                        //fake_label_holding_fade_in_time_for_verse
                        fake_label_holding_fade_in_time_for_verse.setText(return_the_widest_text_for_fade_in_and_fade_out(0, 2, 0.1D));
                        fake_label_holding_fade_in_time_for_verse.setVisible(false);

                        //stack_pane_holding_real_and_fake_fade_in_time_for_verse
                        HBox.setMargin(stack_pane_holding_real_and_fake_fade_in_time_for_verse, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));

                        //fade_out_label_for_verse
                        fade_out_label_for_verse.setText("Fade out:");

                        //fake_fade_out_label_for_verse
                        fake_fade_out_label_for_verse.setText("Fade in:");
                        fake_fade_out_label_for_verse.setVisible(false);

                        //stack_pane_holding_fade_out_and_fake_fade_out_for_verse
                        stack_pane_holding_fade_out_and_fake_fade_out_for_verse.setAlignment(Pos.CENTER_LEFT);

                        //fade_out_slider_for_verse
                        HBox.setMargin(fade_out_slider_for_verse, new Insets(0, 0, 0, 10));
                        HBox.setHgrow(fade_out_slider_for_verse, Priority.ALWAYS);
                        fade_out_slider_for_verse.setMaxWidth(Double.MAX_VALUE);
                        fade_out_slider_for_verse.setMin(0);
                        fade_out_slider_for_verse.setMax(2);
                        set_up_the_tick_marks_for_a_slider(fade_out_slider_for_verse, 0.1);

                        //label_holding_fade_out_time_for_verse
                        label_holding_fade_out_time_for_verse.setText("0.0s");

                        //fake_label_holding_fade_out_time_for_verse
                        fake_label_holding_fade_out_time_for_verse.setText(return_the_widest_text_for_fade_in_and_fade_out(0, 2, 0.1D));
                        fake_label_holding_fade_out_time_for_verse.setVisible(false);

                        //stack_pane_holding_real_and_fake_fade_out_time_for_verse
                        HBox.setMargin(stack_pane_holding_real_and_fake_fade_out_time_for_verse, new Insets(0, 0, 0, left_margin_at_the_start_of_stroke_shadow_weight));

                        //hbox_holding_verse_fade_out_and_slider
                        VBox.setMargin(hbox_holding_verse_fade_out_and_slider, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //separator_under_fade_in_fade_out_verse
                        VBox.setMargin(separator_under_fade_in_fade_out_verse, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        //label_saying_verse
                        label_saying_verse.setText("Verse");

                        //hbox_holding_the_verse_label
                        hbox_holding_the_verse_label.setAlignment(Pos.CENTER_LEFT);
                        VBox.setMargin(hbox_holding_the_verse_label, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));

                        //verse_text_area
                        VBox.setMargin(verse_text_area, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));
                        verse_text_area.setPrefRowCount(3);
                        verse_text_area.setWrapText(true);
                        bind_an_item_to_a_property(verse_text_area, root.widthProperty(), start_and_end_margin * 2);
                        verse_text_area.setNodeOrientation(NodeOrientation.INHERIT);

                        //reset_text_area_and_verse_to_original_verse_button
                        reset_text_area_and_verse_to_original_verse_button.setText("Reset");
                        bind_an_item_to_a_property(reset_text_area_and_verse_to_original_verse_button, root.widthProperty(), start_and_end_margin * 2);
                        VBox.setMargin(reset_text_area_and_verse_to_original_verse_button, new Insets(top_margin_in_vbox_control, start_and_end_margin, 0, start_and_end_margin));


                        //separator_under_shadow
                        VBox.setMargin(separator_under_shadow, new Insets(top_margin_in_vbox_control, separator_start_end, 0, separator_start_end));

                        hbox_holding_the_verse_label.getChildren().add(label_saying_verse);

                        hbox_holding_the_fade_label.getChildren().add(fade_label_at_the_top);

                        stack_pane_holding_fade_in_and_fake_fade_in_for_verse.getChildren().add(fade_in_label_for_verse);
                        stack_pane_holding_fade_in_and_fake_fade_in_for_verse.getChildren().add(fake_fade_in_label_for_verse);

                        stack_pane_holding_fade_out_and_fake_fade_out_for_verse.getChildren().add(fade_out_label_for_verse);
                        stack_pane_holding_fade_out_and_fake_fade_out_for_verse.getChildren().add(fake_fade_out_label_for_verse);

                        stack_pane_holding_real_and_fake_fade_in_time_for_verse.getChildren().add(fake_label_holding_fade_in_time_for_verse);
                        stack_pane_holding_real_and_fake_fade_in_time_for_verse.getChildren().add(label_holding_fade_in_time_for_verse);

                        stack_pane_holding_real_and_fake_fade_out_time_for_verse.getChildren().add(fake_label_holding_fade_out_time_for_verse);
                        stack_pane_holding_real_and_fake_fade_out_time_for_verse.getChildren().add(label_holding_fade_out_time_for_verse);

                        hbox_holding_verse_fade_in_and_slider.getChildren().add(stack_pane_holding_fade_in_and_fake_fade_in_for_verse);
                        hbox_holding_verse_fade_in_and_slider.getChildren().add(fade_in_slider_for_verse);
                        hbox_holding_verse_fade_in_and_slider.getChildren().add(stack_pane_holding_real_and_fake_fade_in_time_for_verse);

                        hbox_holding_verse_fade_out_and_slider.getChildren().add(stack_pane_holding_fade_out_and_fake_fade_out_for_verse);
                        hbox_holding_verse_fade_out_and_slider.getChildren().add(fade_out_slider_for_verse);
                        hbox_holding_verse_fade_out_and_slider.getChildren().add(stack_pane_holding_real_and_fake_fade_out_time_for_verse);


                        stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight.getChildren().add(fake_label_for_shadow_value);
                        stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight.getChildren().add(label_holding_the_shadow_value);

                        hbox_holding_the_shadow_slider_and_value.getChildren().add(label_saying_weight_before_shadow_slider);
                        hbox_holding_the_shadow_slider_and_value.getChildren().add(shadow_slider);
                        hbox_holding_the_shadow_slider_and_value.getChildren().add(stack_pane_holding_the_fake_shadow_weight_and_the_real_shadow_weight);

                        vbox_holding_everything_shadow.getChildren().add(shadow_color_picker);
                        vbox_holding_everything_shadow.getChildren().add(hbox_holding_the_shadow_slider_and_value);

                        hbox_holding_the_shadow_label.getChildren().add(shadow_label);
                        hbox_holding_the_shadow_label.getChildren().add(region_to_expand_space_for_shadow_check_box);
                        hbox_holding_the_shadow_label.getChildren().add(shadow_check_box);


                        hbox_holding_the_advanced_options_toggle.getChildren().add(label_holding_advanced_options);
                        hbox_holding_the_advanced_options_toggle.getChildren().add(toggle_switch_for_advanced_options);


                        hbox_containing_the_plus_minus_for_left_margin.getChildren().add(increase_left_margin_button);
                        hbox_containing_the_plus_minus_for_left_margin.getChildren().add(decrease_left_margin_button);

                        hbox_containing_the_plus_minus_for_right_margin.getChildren().add(increase_right_margin_button);
                        hbox_containing_the_plus_minus_for_right_margin.getChildren().add(decrease_right_margin_button);

                        hbox_hosting_the_left_margin.getChildren().add(pane_holding_left_margin_label);
                        hbox_hosting_the_left_margin.getChildren().add(left_margin_input_field);
                        hbox_hosting_the_left_margin.getChildren().add(hbox_containing_the_plus_minus_for_left_margin);

                        hbox_hosting_the_right_margin.getChildren().add(pane_holding_right_margin_label);
                        hbox_hosting_the_right_margin.getChildren().add(right_margin_input_field);
                        hbox_hosting_the_right_margin.getChildren().add(hbox_containing_the_plus_minus_for_right_margin);


                        hbox_holding_the_margin_label.getChildren().add(margin_label);


                        stack_pane_holding_the_stroke_weights.getChildren().add(fake_label_for_stroke_weight_space);
                        stack_pane_holding_the_stroke_weights.getChildren().add(label_hosting_the_percentage_of_weight);

                        hbox_hosting_reset_and_apply_to_all.getChildren().add(reset_everything_button);
                        hbox_hosting_reset_and_apply_to_all.getChildren().add(separator_between_reset_and_apply_to_all_button);
                        hbox_hosting_reset_and_apply_to_all.getChildren().add(apply_to_all_verses_button);


                        hbox_hosting_the_weight_label_and_the_slider.getChildren().add(label_saying_wieght_beside_slider);
                        hbox_hosting_the_weight_label_and_the_slider.getChildren().add(stroke_weight_slider);
                        hbox_hosting_the_weight_label_and_the_slider.getChildren().add(stack_pane_holding_the_stroke_weights);


                        vbox_carrying_the_stroke_stuff.getChildren().add(stroke_color_picker);
                        vbox_carrying_the_stroke_stuff.getChildren().add(hbox_hosting_the_weight_label_and_the_slider);


                        hbox_for_plus_and_minus.getChildren().add(increase_font_size_button);
                        hbox_for_plus_and_minus.getChildren().add(decrease_font_size_button);

                        hbox_containing_the_text_color_label.getChildren().add(label_saying_color);

                        hbox_hosting_the_stroke_label.getChildren().add(label_saying_stroke);
                        hbox_hosting_the_stroke_label.getChildren().add(region_in_the_middle_of_stuff_horizontal);
                        hbox_hosting_the_stroke_label.getChildren().add(stroke_check_box);

                        hbox_hosting_the_fonts_label.getChildren().add(label_saying_fonts);

                        hbox_hosting_the_position_label.getChildren().add(text_saying_position_above_position);

                        hbox_containing_the_font_size.getChildren().add(label_beside_the_font_size);
                        hbox_containing_the_font_size.getChildren().add(text_field_for_font_size);
                        hbox_containing_the_font_size.getChildren().add(hbox_for_plus_and_minus);

                        hbox_for_x_and_y_positions.getChildren().add(x_label_beside_x_pos);
                        hbox_for_x_and_y_positions.getChildren().add(x_position_of_text);
                        hbox_for_x_and_y_positions.getChildren().add(center_button_x_pos);
                        hbox_for_x_and_y_positions.getChildren().add(y_label_beside_y_pos);
                        hbox_for_x_and_y_positions.getChildren().add(y_position_of_text);
                        hbox_for_x_and_y_positions.getChildren().add(center_button_y_pos);

                        stackPane_of_the_top.getChildren().add(jfxButton);
                        stackPane_of_the_top.getChildren().add(down_or_left_image_view);
                        stackPane_of_the_top.getChildren().add(language_name);

                        v_box_inside_the_stack_pane.getChildren().add(check_box_is_the_langauge_enabled);
                        v_box_inside_the_stack_pane.getChildren().add(separator_between_check_box_and_everything);

                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_hosting_the_position_label);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_for_x_and_y_positions);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(separator_under_position);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_containing_the_text_color_label);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(color_picker);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(separator_under_color_picker);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_hosting_the_fonts_label);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(combox_of_all_of_fonts);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(combox_of_all_of_fonts_sub_choices);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(separator_under_font_picker);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_containing_the_font_size);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(separator_under_font_size);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_holding_the_advanced_options_toggle);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(holds_advnaced_options);

                        /*holds_advnaced_options.getChildren().add(hbox_hosting_the_position_label);
                        holds_advnaced_options.getChildren().add(hbox_for_x_and_y_positions);
                        holds_advnaced_options.getChildren().add(separator_under_position);*/
                        holds_advnaced_options.getChildren().add(separator_inside_advanced_options_before_everything);
                        holds_advnaced_options.getChildren().add(hbox_holding_the_fade_label);
                        holds_advnaced_options.getChildren().add(hbox_holding_verse_fade_in_and_slider);
                        holds_advnaced_options.getChildren().add(hbox_holding_verse_fade_out_and_slider);
                        holds_advnaced_options.getChildren().add(separator_under_fade_in_fade_out_verse);
                        holds_advnaced_options.getChildren().add(hbox_hosting_the_stroke_label);
                        holds_advnaced_options.getChildren().add(vbox_carrying_the_stroke_stuff);
                        holds_advnaced_options.getChildren().add(separator_under_stroke);
                        /*holds_advnaced_options.getChildren().add(hbox_holding_the_margin_label);
                        holds_advnaced_options.getChildren().add(hbox_hosting_the_left_margin);
                        holds_advnaced_options.getChildren().add(hbox_hosting_the_right_margin);*/
                        holds_advnaced_options.getChildren().add(hbox_holding_the_shadow_label);
                        holds_advnaced_options.getChildren().add(vbox_holding_everything_shadow);
                        holds_advnaced_options.getChildren().add(separator_under_shadow);
                        holds_advnaced_options.getChildren().add(hbox_holding_the_verse_label);
                        holds_advnaced_options.getChildren().add(verse_text_area);
                        holds_advnaced_options.getChildren().add(reset_text_area_and_verse_to_original_verse_button);
                        v_box_inside_the_stack_pane.getChildren().add(v_box_with_all_of_the_controls_except_check_box);

                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(separator_under_advanced_options);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(hbox_hosting_reset_and_apply_to_all);
                        v_box_with_all_of_the_controls_except_check_box.getChildren().add(item_at_the_bottom_of_extended_pane);


                        stackPane_extended_with_all_of_the_info.getChildren().add(v_box_inside_the_stack_pane);
                        root.getChildren().add(stackPane_of_the_top);
                        root.getChildren().add(stackPane_extended_with_all_of_the_info);

                        itemProperty().addListener(new ChangeListener<Language_info>() {
                            @Override
                            public void changed(ObservableValue<? extends Language_info> observableValue, Language_info old_language_info, Language_info new_language_info) {
                                if (old_language_info != null) {
                                    if (old_language_info.getX_position_change_listener() != null) {
                                        x_position_of_text.textProperty().removeListener(old_language_info.getX_position_change_listener());
                                        old_language_info.setX_position_change_listener(null);
                                    }
                                    if (old_language_info.getY_position_change_listener() != null) {
                                        y_position_of_text.textProperty().removeListener(old_language_info.getY_position_change_listener());
                                        old_language_info.setY_position_change_listener(null);
                                    }
                                    if (old_language_info.getColor_change_listener() != null) {
                                        color_picker.valueProperty().removeListener(old_language_info.getColor_change_listener());
                                        old_language_info.setColor_change_listener(null);
                                    }
                                    if (old_language_info.getFont_size_change_listener() != null) {
                                        text_field_for_font_size.textProperty().removeListener(old_language_info.getFont_size_change_listener());
                                        old_language_info.setFont_size_change_listener(null);
                                    }
                                    if (old_language_info.getFont_change_listener() != null) {
                                        combox_of_all_of_fonts.valueProperty().removeListener(old_language_info.getFont_change_listener());
                                        old_language_info.setFont_change_listener(null);
                                    }
                                    if (old_language_info.getSub_font_change_listener() != null) {
                                        combox_of_all_of_fonts_sub_choices.valueProperty().removeListener(old_language_info.getSub_font_change_listener());
                                        old_language_info.setSub_font_change_listener(null);
                                    }
                                    if (old_language_info.getStroke_color_change_listener() != null) {
                                        stroke_color_picker.valueProperty().removeListener(old_language_info.getStroke_color_change_listener());
                                        old_language_info.setStroke_color_change_listener(null);
                                    }
                                    if (old_language_info.getStroke_weight_change_listener() != null) {
                                        stroke_weight_slider.valueProperty().removeListener(old_language_info.getStroke_weight_change_listener());
                                        old_language_info.setStroke_weight_change_listener(null);
                                    }
                                    if (old_language_info.getAdvanced_options_change_listener() != null) {
                                        toggle_switch_for_advanced_options.selectedProperty().removeListener(old_language_info.getAdvanced_options_change_listener());
                                        old_language_info.setAdvanced_options_change_listener(null);
                                    }
                                    if (old_language_info.getShadow_color_change_listener() != null) {
                                        shadow_color_picker.valueProperty().removeListener(old_language_info.getShadow_color_change_listener());
                                        old_language_info.setShadow_color_change_listener(null);
                                    }
                                    if (old_language_info.getShadow_weight_change_listener() != null) {
                                        shadow_slider.valueProperty().removeListener(old_language_info.getShadow_weight_change_listener());
                                        old_language_info.setShadow_weight_change_listener(null);
                                    }
                                    if (old_language_info.getVerse_fade_in_listener() != null) {
                                        fade_in_slider_for_verse.valueProperty().removeListener(old_language_info.getVerse_fade_in_listener());
                                        old_language_info.setVerse_fade_in_listener(null);
                                    }
                                    if (old_language_info.getVerse_fade_out_listener() != null) {
                                        fade_out_slider_for_verse.valueProperty().removeListener(old_language_info.getVerse_fade_out_listener());
                                        old_language_info.setVerse_fade_out_listener(null);
                                    }
                                    if (old_language_info.getVerse_text_area_text_change_listener() != null) {
                                        verse_text_area.textProperty().removeListener(old_language_info.getVerse_text_area_text_change_listener());
                                        old_language_info.setVerse_text_area_text_change_listener(null);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Language_info item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Text_item text_item_of_the_selected_verse = item.getArrayList_of_all_of_the_translations().get(selected_verse);
                            {
                                make_the_language_translation_extended(stackPane_extended_with_all_of_the_info, down_or_left_image_view, item, item.isItem_extended());
                                if (item.getLanguage_name().equals("arabic")) {
                                    check_box_is_the_langauge_enabled.setText("Show text");
                                } else {
                                    check_box_is_the_langauge_enabled.setText("Show translation");
                                }
                                select_or_un_select_the_language(item, jfxButton, language_name, check_box_is_the_langauge_enabled, down_or_left_image_view, helloController, v_box_with_all_of_the_controls_except_check_box);
                                color_picker.setValue(text_item_of_the_selected_verse.getColor());
                                String main_font_name = text_item_of_the_selected_verse.getFont().getTypeface().getFamilyName();
                                combox_of_all_of_fonts.setValue(main_font_name);
                                add_all_sub_fonts_to_sub_fonts_combo_box(combox_of_all_of_fonts, combox_of_all_of_fonts_sub_choices);
                                set_the_correct_sub_font(combox_of_all_of_fonts_sub_choices, text_item_of_the_selected_verse.getFont());
                                text_field_for_font_size.setText(String.valueOf((int) text_item_of_the_selected_verse.getFont_size()));
                                if (item.isAdvanced_options_selected()) {
                                    holds_advnaced_options.setVisible(true);
                                    holds_advnaced_options.setManaged(true);
                                    toggle_switch_for_advanced_options.setSelected(true);
                                } else {
                                    holds_advnaced_options.setVisible(false);
                                    holds_advnaced_options.setManaged(false);
                                    toggle_switch_for_advanced_options.setSelected(false);
                                }
                                Point2D point2D_of_the_text = text_item_of_the_selected_verse.getText_box_info().getCenter_position();
                                x_position_of_text.setText(String.valueOf((int) point2D_of_the_text.getX()));
                                y_position_of_text.setText(String.valueOf((int) point2D_of_the_text.getY()));
                                left_margin_input_field.setText(remove_trailing_zeroes_from_number(text_item_of_the_selected_verse.getLeft_margin()));
                                right_margin_input_field.setText(remove_trailing_zeroes_from_number(text_item_of_the_selected_verse.getRight_margin()));
                                Text_accessory_info stroke_info = text_item_of_the_selected_verse.getStroke_info();
                                if (stroke_info.isIs_the_accessory_on()) {
                                    vbox_carrying_the_stroke_stuff.setDisable(false);
                                    stroke_check_box.setSelected(true);
                                } else {
                                    vbox_carrying_the_stroke_stuff.setDisable(true);
                                    stroke_check_box.setSelected(false);
                                }
                                stroke_color_picker.setValue(stroke_info.getAccessory_color());
                                stroke_weight_slider.setValue(stroke_info.getAccessory_weight());
                                label_hosting_the_percentage_of_weight.setText(return_formatted_string_to_1_decimal_place_always(stroke_info.getAccessory_weight()));

                                Text_accessory_info shadow_info = text_item_of_the_selected_verse.getShadow_info();
                                if (shadow_info.isIs_the_accessory_on()) {
                                    vbox_holding_everything_shadow.setDisable(false);
                                    shadow_check_box.setSelected(true);
                                } else {
                                    vbox_holding_everything_shadow.setDisable(true);
                                    shadow_check_box.setSelected(false);
                                }
                                shadow_color_picker.setValue(shadow_info.getAccessory_color());
                                shadow_slider.setValue(shadow_info.getAccessory_weight());
                                label_holding_the_shadow_value.setText(return_formatted_string_to_1_decimal_place_always(shadow_info.getAccessory_weight()));

                                fade_in_slider_for_verse.setValue(text_item_of_the_selected_verse.getFade_in());
                                label_holding_fade_in_time_for_verse.setText(return_formatted_string_to_1_decimal_place_always(text_item_of_the_selected_verse.getFade_in()).concat(unit_sign_beside_fade_in_fade_out));

                                fade_out_slider_for_verse.setValue(text_item_of_the_selected_verse.getFade_out());
                                label_holding_fade_out_time_for_verse.setText(return_formatted_string_to_1_decimal_place_always(text_item_of_the_selected_verse.getFade_out()).concat(unit_sign_beside_fade_in_fade_out));

                                verse_text_area.setText(text_item_of_the_selected_verse.getVerse_text());
                            }
                            jfxButton.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    if (item.isItem_extended()) {
                                        make_the_language_translation_extended(stackPane_extended_with_all_of_the_info, down_or_left_image_view, item, false);
                                        item.setItem_extended(false);
                                    } else {
                                        make_the_language_translation_extended(stackPane_extended_with_all_of_the_info, down_or_left_image_view, item, true);
                                        item.setItem_extended(true);
                                    }
                                    send_language_expanded_analytics_event(item.getLanguage_name());
                                }
                            });

                            check_box_is_the_langauge_enabled.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    item.setVisible_check_mark_checked(check_box_is_the_langauge_enabled.isSelected());
                                    select_or_un_select_the_language(item, jfxButton, language_name, check_box_is_the_langauge_enabled, down_or_left_image_view, helloController, v_box_with_all_of_the_controls_except_check_box);
                                    set_up_or_hide_the_canvas(helloController, item);
                                    send_language_selected_analytics_event(item.getLanguage_name());
                                }
                            });

                            increase_font_size_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_text_size_by_increment(text_field_for_font_size, text_item_of_the_selected_verse, item, plus_minus_font_increments);
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("font_size_changed");
                                }
                            });

                            decrease_font_size_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_text_size_by_increment(text_field_for_font_size, text_item_of_the_selected_verse, item, -plus_minus_font_increments);
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("font_size_changed");
                                }
                            });

                            stroke_check_box.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    vbox_carrying_the_stroke_stuff.setDisable(!stroke_check_box.isSelected());
                                    text_item_of_the_selected_verse.getStroke_info().setIs_the_accessory_on(stroke_check_box.isSelected());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("stroke_checked");
                                }
                            });

                            /*increase_left_margin_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_margin_by_increment(left_margin_input_field, text_item_of_the_selected_verse, item, plus_minus_font_increments, Margin_type.LEFT_MARGIN);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            });

                            decrease_left_margin_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_margin_by_increment(left_margin_input_field, text_item_of_the_selected_verse, item, -plus_minus_font_increments, Margin_type.LEFT_MARGIN);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            });

                            increase_right_margin_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_margin_by_increment(right_margin_input_field, text_item_of_the_selected_verse, item, plus_minus_font_increments, Margin_type.RIGHT_MARGIN);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            });

                            decrease_right_margin_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    change_margin_by_increment(right_margin_input_field, text_item_of_the_selected_verse, item, -plus_minus_font_increments, Margin_type.RIGHT_MARGIN);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            });*/

                            center_button_x_pos.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    double y_pos = text_item_of_the_selected_verse.getText_box_info().getCenter_position().getY();
                                    double x_pos = helloController.canvas_displaying_the_verses.getWidth() / 2D;
                                    text_item_of_the_selected_verse.getText_box_info().setCenter_position(new Point2D(x_pos, y_pos));
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    x_position_of_text.setText(String.valueOf((int) x_pos));
                                    send_analytics_event("position_centered");
                                }
                            });

                            center_button_y_pos.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    double x_pos = text_item_of_the_selected_verse.getText_box_info().getCenter_position().getX();
                                    double y_pos = helloController.canvas_displaying_the_verses.getHeight() / 2D;
                                    text_item_of_the_selected_verse.getText_box_info().setCenter_position(new Point2D(x_pos, y_pos));
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    y_position_of_text.setText(String.valueOf((int) y_pos));
                                    send_analytics_event("position_centered");
                                }
                            });

                            shadow_check_box.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    vbox_holding_everything_shadow.setDisable(!shadow_check_box.isSelected());
                                    text_item_of_the_selected_verse.getShadow_info().setIs_the_accessory_on(shadow_check_box.isSelected());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    send_analytics_event("shadow_checked");
                                }
                            });

                            reset_text_area_and_verse_to_original_verse_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    text_item_of_the_selected_verse.setVerse_text(text_item_of_the_selected_verse.getOriginal_verse_text());
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    verse_text_area.setText(text_item_of_the_selected_verse.getOriginal_verse_text());
                                }
                            });

                            reset_everything_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    reset_verse_settings(text_item_of_the_selected_verse);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    helloController.list_view_with_all_of_the_languages.refresh();
                                    send_analytics_event("reset_everything_used");
                                }
                            });

                            apply_to_all_verses_button.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    apply_to_all_verse_settings(text_item_of_the_selected_verse, item.getArrayList_of_all_of_the_translations());
                                    send_analytics_event("apply_to_all_used");
                                }
                            });

                            //x_position_text_field_listener
                            ChangeListener<String> x_pos_text_feild_change_listener = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    Text_item local_text_item = item.getArrayList_of_all_of_the_translations().get(selected_verse);
                                    double new_x_pos = 0;
                                    if (!new_string.isEmpty()) {
                                        new_x_pos = Double.parseDouble(new_string);
                                    }
                                    local_text_item.getText_box_info().set_x_position(new_x_pos);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("position_changed");
                                }
                            };
                            x_position_of_text.textProperty().addListener(x_pos_text_feild_change_listener);
                            item.setX_position_change_listener(x_pos_text_feild_change_listener);

                            //y_position_text_field_listener
                            ChangeListener<String> y_pos_text_feild_change_listener = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    Text_item local_text_item = item.getArrayList_of_all_of_the_translations().get(selected_verse);
                                    double new_y_pos = 0;
                                    if (!new_string.isEmpty()) {
                                        new_y_pos = Double.parseDouble(new_string);
                                    }
                                    local_text_item.getText_box_info().set_y_position(new_y_pos);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("position_changed");
                                }
                            };
                            y_position_of_text.textProperty().addListener(y_pos_text_feild_change_listener);
                            item.setY_position_change_listener(y_pos_text_feild_change_listener);

                            //color_picker_change_listen
                            ChangeListener<? super javafx.scene.paint.Color> color_picker_change_listener = new ChangeListener<javafx.scene.paint.Color>() {
                                @Override
                                public void changed(ObservableValue<? extends javafx.scene.paint.Color> observableValue, javafx.scene.paint.Color old_color, javafx.scene.paint.Color new_color) {
                                    text_item_of_the_selected_verse.setColor(new_color);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("color_changed");
                                }
                            };
                            color_picker.valueProperty().addListener(color_picker_change_listener);
                            item.setColor_change_listener(color_picker_change_listener);

                            //font size change listeners
                            ChangeListener<String> font_size_change_listen = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    double font_size = 0;
                                    if (!new_string.isEmpty()) {
                                        font_size = Double.parseDouble(new_string);
                                    }
                                    text_item_of_the_selected_verse.setFont_size(font_size);
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("font_size_changed");
                                }
                            };
                            text_field_for_font_size.textProperty().addListener(font_size_change_listen);
                            item.setFont_size_change_listener(font_size_change_listen);

                            //font_change_listener
                            ChangeListener<String> change_listener_for_font = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_font, String new_font) {
                                    add_all_sub_fonts_to_sub_fonts_combo_box(combox_of_all_of_fonts, combox_of_all_of_fonts_sub_choices);
                                    combox_of_all_of_fonts_sub_choices.getSelectionModel().select(0);
                                    text_item_of_the_selected_verse.setFont(new_font, combox_of_all_of_fonts_sub_choices.getItems().getFirst());
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    //combox_of_all_of_fonts_sub_choices.setVisibleRowCount(sub_fonts.getFont_names().size());
                                    send_analytics_event("font_changed");
                                }
                            };
                            combox_of_all_of_fonts.valueProperty().addListener(change_listener_for_font);
                            item.setFont_change_listener(change_listener_for_font);

                            //sub_font_cahnge_listener
                            ChangeListener<Sub_font_name_and_style> change_listener_for_sub_font = new ChangeListener<Sub_font_name_and_style>() {
                                @Override
                                public void changed(ObservableValue<? extends Sub_font_name_and_style> observableValue, Sub_font_name_and_style old_sub_font_name_and_style, Sub_font_name_and_style new_sub_font_name_and_style) {
                                    if (new_sub_font_name_and_style != null) {
                                        text_item_of_the_selected_verse.setFont(combox_of_all_of_fonts.getSelectionModel().getSelectedItem(), new_sub_font_name_and_style);
                                        text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                        place_the_canvas_text(helloController);
                                        place_the_box_surrounding_the_text(helloController, item);
                                        item.setText_box_showing(true);
                                        send_analytics_event("sub_font_changed");
                                    }
                                }
                            };
                            combox_of_all_of_fonts_sub_choices.valueProperty().addListener(change_listener_for_sub_font);
                            item.setSub_font_change_listener(change_listener_for_sub_font);

                            //stroke_color_change_listener
                            ChangeListener<? super javafx.scene.paint.Color> change_listener_for_stroke_color = new ChangeListener<javafx.scene.paint.Color>() {
                                @Override
                                public void changed(ObservableValue<? extends javafx.scene.paint.Color> observableValue, javafx.scene.paint.Color old_color, javafx.scene.paint.Color new_color) {
                                    text_item_of_the_selected_verse.getStroke_info().setAccessory_color(new_color);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("stroke_adjusted");
                                }
                            };
                            stroke_color_picker.valueProperty().addListener(change_listener_for_stroke_color);
                            item.setStroke_color_change_listener(change_listener_for_stroke_color);

                            //stroke_weight_change_listener
                            ChangeListener<? super Number> change_listener_for_stroke_weight = new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                                    text_item_of_the_selected_verse.getStroke_info().setAccessory_weight(new_number.doubleValue());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    label_hosting_the_percentage_of_weight.setText(return_formatted_string_to_1_decimal_place_always(stroke_weight_slider.getValue()));
                                    send_analytics_event("stroke_adjusted");
                                }
                            };
                            stroke_weight_slider.valueProperty().addListener(change_listener_for_stroke_weight);
                            item.setStroke_weight_change_listener(change_listener_for_stroke_weight);

                            //advanced_options_selected_listener
                            ChangeListener<? super Boolean> change_listener_for_advanced_options = new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_value, Boolean new_value) {
                                    holds_advnaced_options.setVisible(new_value);
                                    holds_advnaced_options.setManaged(new_value);
                                    item.setAdvanced_options_selected(new_value);
                                    send_analytics_event("advanced_options_selected");
                                }
                            };
                            toggle_switch_for_advanced_options.selectedProperty().addListener(change_listener_for_advanced_options);
                            item.setAdvanced_options_change_listener(change_listener_for_advanced_options);

                            //shadow_color_change_listener
                            ChangeListener<? super javafx.scene.paint.Color> shadow_color_change_listener = new ChangeListener<javafx.scene.paint.Color>() {
                                @Override
                                public void changed(ObservableValue<? extends javafx.scene.paint.Color> observableValue, javafx.scene.paint.Color old_color, javafx.scene.paint.Color new_color) {
                                    text_item_of_the_selected_verse.getShadow_info().setAccessory_color(new_color);
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("shadow_adjusted");
                                }
                            };
                            shadow_color_picker.valueProperty().addListener(shadow_color_change_listener);
                            item.setShadow_color_change_listener(shadow_color_change_listener);

                            //shadow_weight_change_listener
                            ChangeListener<? super Number> shadow_weight_change_listener = new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                                    label_holding_the_shadow_value.setText(return_formatted_string_to_1_decimal_place_always(new_number.doubleValue()));
                                    text_item_of_the_selected_verse.getShadow_info().setAccessory_weight(new_number.doubleValue());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("shadow_adjusted");
                                }
                            };
                            shadow_slider.valueProperty().addListener(shadow_weight_change_listener);
                            item.setShadow_weight_change_listener(shadow_weight_change_listener);

                            //verse_fade_in_listener
                            ChangeListener<? super Number> verse_fade_in_listener = new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                                    label_holding_fade_in_time_for_verse.setText(return_formatted_string_to_1_decimal_place_always(new_number.doubleValue()) + unit_sign_beside_fade_in_fade_out);
                                    text_item_of_the_selected_verse.setFade_in(new_number.doubleValue());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    send_analytics_event("fade_adjusted");
                                }
                            };
                            fade_in_slider_for_verse.valueProperty().addListener(verse_fade_in_listener);
                            item.setVerse_fade_in_listener(verse_fade_in_listener);

                            //verse_fade_out_listener
                            ChangeListener<? super Number> verse_fade_out_listener = new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                                    label_holding_fade_out_time_for_verse.setText(return_formatted_string_to_1_decimal_place_always(new_number.doubleValue()) + unit_sign_beside_fade_in_fade_out);
                                    text_item_of_the_selected_verse.setFade_out(new_number.doubleValue());
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    send_analytics_event("fade_adjusted");
                                }
                            };
                            fade_out_slider_for_verse.valueProperty().addListener(verse_fade_out_listener);
                            item.setVerse_fade_out_listener(verse_fade_out_listener);

                            //text_area_change_listener
                            ChangeListener<? super String> verse_text_area_change_listener = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    text_item_of_the_selected_verse.setVerse_text(new_string);
                                    text_item_of_the_selected_verse.getText_box_info().update_the_min_height_and_width();
                                    place_the_canvas_text(helloController);
                                    place_the_box_surrounding_the_text(helloController, item);
                                    item.setText_box_showing(true);
                                    send_analytics_event("verse_text_display_updated");
                                }
                            };
                            verse_text_area.textProperty().addListener(verse_text_area_change_listener);
                            item.setVerse_text_area_text_change_listener(verse_text_area_change_listener);

                            //left_margin_listener
                            /*ChangeListener<String> change_listener_for_left_margin_text = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    double left_margin = 0;
                                    if (!new_string.isEmpty()) {
                                        left_margin = Double.parseDouble(new_string);
                                    }
                                    text_item_of_the_selected_verse.setRight_margin(left_margin);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), left_margin, text_item_of_the_selected_verse.getRight_margin()));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            };
                            left_margin_input_field.textProperty().addListener(change_listener_for_left_margin_text);
                            item.setLeft_margin_text_change_listener(change_listener_for_left_margin_text);*/

                            //right_margin_listener
                            /*ChangeListener<String> change_listener_for_right_margin_text = new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observableValue, String old_string, String new_string) {
                                    double right_margin = 0;
                                    if (!new_string.isEmpty()) {
                                        right_margin = Double.parseDouble(new_string);
                                    }
                                    text_item_of_the_selected_verse.setRight_margin(right_margin);
                                    text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), right_margin));
                                    place_the_canvas_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
                                }
                            };
                            right_margin_input_field.textProperty().addListener(change_listener_for_right_margin_text);
                            item.setRight_margin_text_change_listener(change_listener_for_right_margin_text);*/
                            setGraphic(root);
                        }
                    }
                };
            }
        });
    }

    /*private void remove_all_the_language_info_listeners(Translation_UI_fields translation_ui_fields) {
        Language_info language_info = translation_ui_fields.getLanguage_info();
        if (language_info != null) {
            if (language_info.getX_position_change_listener() != null) {
                translation_ui_fields.getX_position_of_text().textProperty().removeListener(language_info.getX_position_change_listener());
                language_info.setX_position_change_listener(null);
            }
            if (language_info.getY_position_change_listener() != null) {
                translation_ui_fields.getY_position_of_text().textProperty().removeListener(language_info.getY_position_change_listener());
                language_info.setY_position_change_listener(null);
            }
            if (language_info.getColor_change_listener() != null) {
                translation_ui_fields.getColor_picker().valueProperty().removeListener(language_info.getColor_change_listener());
                language_info.setColor_change_listener(null);
            }
            if (language_info.getFont_size_change_listener() != null) {
                translation_ui_fields.getText_field_for_font_size().textProperty().removeListener(language_info.getFont_size_change_listener());
                language_info.setFont_size_change_listener(null);
            }
            if (language_info.getFont_change_listener() != null) {
                translation_ui_fields.getCombox_of_all_of_fonts().valueProperty().removeListener(language_info.getFont_change_listener());
                language_info.setFont_change_listener(null);
            }
            if (language_info.getSub_font_change_listener() != null) {
                translation_ui_fields.getCombox_of_all_of_fonts_sub_choices().valueProperty().removeListener(language_info.getSub_font_change_listener());
                language_info.setSub_font_change_listener(null);
            }
            if (language_info.getStroke_color_change_listener() != null) {
                translation_ui_fields.getStroke_color_picker().valueProperty().removeListener(language_info.getStroke_color_change_listener());
                language_info.setStroke_color_change_listener(null);
            }
            if (language_info.getStroke_weight_change_listener() != null) {
                translation_ui_fields.getStroke_weight_slider().valueProperty().removeListener(language_info.getStroke_weight_change_listener());
                language_info.setStroke_weight_change_listener(null);
            }
            if (language_info.getAdvanced_options_change_listener() != null) {
                translation_ui_fields.getToggle_switch_for_advanced_options().selectedProperty().removeListener(language_info.getAdvanced_options_change_listener());
                language_info.setAdvanced_options_change_listener(null);
            }
            if (language_info.getShadow_color_change_listener() != null) {
                translation_ui_fields.getShadow_color_picker().valueProperty().removeListener(language_info.getShadow_color_change_listener());
                language_info.setShadow_color_change_listener(null);
            }
            if (language_info.getShadow_weight_change_listener() != null) {
                translation_ui_fields.getShadow_slider().valueProperty().removeListener(language_info.getShadow_weight_change_listener());
                language_info.setShadow_weight_change_listener(null);
            }
            if (language_info.getVerse_fade_in_listener() != null) {
                translation_ui_fields.getFade_in_slider_for_verse().valueProperty().removeListener(language_info.getVerse_fade_in_listener());
                language_info.setVerse_fade_in_listener(null);
            }
            if (language_info.getVerse_fade_out_listener() != null) {
                translation_ui_fields.getFade_out_slider_for_verse().valueProperty().removeListener(language_info.getVerse_fade_out_listener());
                language_info.setVerse_fade_out_listener(null);
            }
            if (language_info.getVerse_text_area_text_change_listener() != null) {
                translation_ui_fields.getVerse_text_area().textProperty().removeListener(language_info.getVerse_text_area_text_change_listener());
                language_info.setVerse_text_area_text_change_listener(null);
            }
        }
    }*/

    private void reset_verse_settings(Text_item text_item) {
        text_item.reset_text_item();
    }

    private void apply_to_all_verse_settings(Text_item text_item, ArrayList<Text_item> array_list_of_all_of_the_text_items) {
        for (int i = 0; i < array_list_of_all_of_the_text_items.size(); i++) {
            Text_item local_text_item = array_list_of_all_of_the_text_items.get(i);
            if (local_text_item.equals(text_item)) {
                continue;
            }
            array_list_of_all_of_the_text_items.set(i, new Text_item(text_item, local_text_item.getOriginal_verse_text(), local_text_item.getOriginal_verse_text()));
        }
    }

    private void change_text_size_by_increment(TextField text_field_for_font_size, Text_item text_item_of_the_selected_verse, Language_info item, double plus_minus_font_increments_local) {
        String verse_text = text_item_of_the_selected_verse.getVerse_text();
        int caret_position = text_field_for_font_size.getCaretPosition();
        String font_text_field_input = text_field_for_font_size.getText();
        double font_size = 0;
        if (!font_text_field_input.isEmpty()) {
            font_size = Double.parseDouble(font_text_field_input);
        }
        font_size = font_size + plus_minus_font_increments_local;
        text_field_for_font_size.setText(remove_trailing_zeroes_from_number(font_size));
        text_item_of_the_selected_verse.setFont_size(font_size);
        //text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(verse_text, text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
        text_field_for_font_size.positionCaret(Math.min(caret_position, text_field_for_font_size.getText().length()));
    }

    /*private void change_margin_by_increment(HelloController helloController, TextField text_field_for_left_margin, Text_item text_item_of_the_selected_verse, Language_info item, double plus_minus_font_increments_local, Margin_type margin_type) {
        String verse_text = text_item_of_the_selected_verse.getVerse_text();
        int caret_position = text_field_for_left_margin.getCaretPosition();
        String font_text_field_input = text_field_for_left_margin.getText();
        double margin = 0;
        if (!font_text_field_input.isEmpty()) {
            margin = Double.parseDouble(font_text_field_input);
        }
        margin = margin + plus_minus_font_increments_local;
        text_field_for_left_margin.setText(remove_trailing_zeroes_from_number(margin));
        if (margin_type == Margin_type.LEFT_MARGIN) {
            text_item_of_the_selected_verse.setLeft_margin(margin);
        } else if (margin_type == Margin_type.RIGHT_MARGIN) {
            text_item_of_the_selected_verse.setRight_margin(margin);
        }
        text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(verse_text, text_item_of_the_selected_verse.getFont(), item.getLanguage_canvas().getWidth(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
        remove_all_of_the_text_boxes(helloController, helloController.list_view_with_all_of_the_languages);
        place_the_canvas_text(helloController);
        place_the_box_surrounding_the_text(item.getLanguage_canvas(), text_item_of_the_selected_verse);
        item.setText_box_showing(true);
        text_field_for_left_margin.positionCaret(Math.min(caret_position, text_field_for_left_margin.getText().length()));
    }*/

    private void set_up_or_hide_the_canvas(HelloController helloController, Language_info language_info) {
        Text_item text_item = language_info.getArrayList_of_all_of_the_translations().get(selected_verse);
        if (language_info.isVisible_check_mark_checked()) {
            set_up_the_adjusted_text(text_item);
            place_the_canvas_text(helloController);
            place_the_box_surrounding_the_text(helloController, language_info);
            language_info.setText_box_showing(true);
        } else {
            place_the_canvas_text(helloController);
        }
    }

    private void set_up_the_adjusted_text(Text_item text_item_of_the_selected_verse) {
        text_item_of_the_selected_verse.setAdjusted_verse_text(Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item_of_the_selected_verse.getVerse_text(), text_item_of_the_selected_verse.getFont(), text_item_of_the_selected_verse.getText_box_info().getText_box_width() - text_item_of_the_selected_verse.getExtra_width_padding(), text_item_of_the_selected_verse.getLeft_margin(), text_item_of_the_selected_verse.getRight_margin()));
    }

    private void set_the_correct_sub_font(ComboBox<Sub_font_name_and_style> combo_box_of_sub_fonts, io.github.humbleui.skija.Font font) {
        if (font.getTypeface() != null) {
            FontStyle font_style = font.getTypeface().getFontStyle();
            ;
            for (Sub_font_name_and_style sub_font_name_and_style : combo_box_of_sub_fonts.getItems()) {
                if (font_style.equals(sub_font_name_and_style.getFont_style())) {
                    combo_box_of_sub_fonts.getSelectionModel().select(sub_font_name_and_style);
                    return;
                }
            }
        }
    }

    private void update_the_translations(HelloController helloController, HashMap<String, ArrayList<String>> hash_map_with_allf_of_the_verses) {
        ObservableList<Language_info> arrayList_from_the_list = helloController.list_view_with_all_of_the_languages.getItems();
        for (int i = 0; i < arrayList_from_the_list.size(); i++) {
            Language_info languageInfo = arrayList_from_the_list.get(i);
            String language_name = languageInfo.getLanguage_name();
            ArrayList<String> array_list_with_verses = hash_map_with_allf_of_the_verses.get(language_name);
            if (array_list_with_verses != null) {
                ArrayList<Text_item> array_list_with_text_items = new ArrayList<>(array_list_with_verses.size());
                for (int j = 0; j < array_list_with_verses.size(); j++) {
                    Text_item text_item = new Text_item(edit_the_verses_before_adding_them(array_list_with_verses.get(j)));
                    array_list_with_text_items.add(text_item);
                }
                languageInfo.setArrayList_of_all_of_the_translations(array_list_with_text_items);
            } else {
                System.err.println("A translation wasn't found in the hash map. The language is: " + language_name);
            }
        }
    }

    private void make_list_view_selection_model_null(HelloController helloController) {
        helloController.list_view_with_all_of_the_languages.setSelectionModel(null);
    }

    private void make_the_language_translation_extended(StackPane stackPane, ImageView imageView, Language_info language_info, boolean extended) {
        if (extended) {
            stackPane.setVisible(true);
            stackPane.setManaged(true);
            imageView.setRotate(90);
        } else {
            stackPane.setVisible(false);
            stackPane.setManaged(false);
            imageView.setRotate(0);
        }
    }

    private void select_or_un_select_the_language(Language_info item, JFXButton jfxButton, Label language_name, CheckBox check_box_is_the_langauge_enabled, ImageView down_or_left_image_view, HelloController helloController, VBox v_box_with_all_of_the_controls_except_check_box) {
        if (item.isVisible_check_mark_checked()) {
            language_name.setText(item.getDisplayed_language_name());
            language_name.setTextFill(javafx.scene.paint.Color.WHITE);
            jfxButton.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
            check_box_is_the_langauge_enabled.setSelected(true);
            down_or_left_image_view.setImage(return_white_logo_from_black("arrow_forward_ios", (int) down_or_left_image_view.getFitWidth(), (int) down_or_left_image_view.getFitHeight()));
            v_box_with_all_of_the_controls_except_check_box.setDisable(false);
        } else {
            language_name.setText(item.getDisplayed_language_name());
            language_name.setTextFill(javafx.scene.paint.Color.BLACK);
            jfxButton.setStyle("-fx-background-color: #00000000;");
            check_box_is_the_langauge_enabled.setSelected(false);
            down_or_left_image_view.setImage(return_the_image_to_be_the_icon("arrow_forward_ios", (int) down_or_left_image_view.getFitWidth(), (int) down_or_left_image_view.getFitHeight()));
            v_box_with_all_of_the_controls_except_check_box.setDisable(true);
        }
    }

    private Image return_white_logo_from_black(String icon_name, int width, int height) {
        Image normal_icon = return_the_image_to_be_the_icon(icon_name, width, height);
        WritableImage writable_image_to_be_returned = new WritableImage((int) normal_icon.getWidth(), (int) normal_icon.getHeight());
        PixelReader pixelReader = normal_icon.getPixelReader();
        PixelWriter pixelWriter = writable_image_to_be_returned.getPixelWriter();
        int writeable_image_width = (int) writable_image_to_be_returned.getWidth();
        int writeable_image_height = (int) writable_image_to_be_returned.getHeight();
        for (int i = 0; i < writeable_image_width; i++) {
            for (int j = 0; j < writeable_image_height; j++) {
                if (pixelReader.getColor(i, j).getOpacity() == 0) {
                    pixelWriter.setColor(i, j, javafx.scene.paint.Color.TRANSPARENT);
                } else {
                    javafx.scene.paint.Color color = pixelReader.getColor(i, j);
                    double red = color.getRed();
                    double green = color.getGreen();
                    double blue = color.getBlue();
                    javafx.scene.paint.Color new_color = new javafx.scene.paint.Color(1D - red, 1D - green, 1D - blue, color.getOpacity());
                    pixelWriter.setColor(i, j, new_color);
                }
            }
        }
        return writable_image_to_be_returned;
    }

    private void place_the_canvas_text(HelloController helloController) {
        GraphicsContext gc = helloController.canvas_displaying_the_verses.getGraphicsContext2D();
        gc.clearRect(0, 0, helloController.canvas_displaying_the_verses.getWidth(), helloController.canvas_displaying_the_verses.getHeight());

        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        double polygon_x_pos = return_polygon_middle_position(time_line_pane_data);
        long x_pos_time = pixels_to_nanoseconds(time_line_pane_data, polygon_x_pos - time_line_pane_data.getTime_line_base_line());

        for (Language_info language_info : helloController.list_view_with_all_of_the_languages.getItems()) {
            if (language_info.isVisible_check_mark_checked()) {
                place_the_canvas_text(helloController, helloController.canvas_displaying_the_verses, language_info.getArrayList_of_all_of_the_translations().get(selected_verse), x_pos_time);
            }
        }
        if (helloController.check_box_saying_help_spread_the_app.isSelected()) {
            Text_item made_with_sabrly_text_item = get_the_made_with_sabrly_text_item(helloController.canvas_displaying_the_verses.getWidth() / 2D, helloController.canvas_displaying_the_verses.getHeight() * 0.95D);
            place_the_canvas_text(helloController, helloController.canvas_displaying_the_verses, made_with_sabrly_text_item, 0);
        }
    }

    private void place_the_canvas_text(HelloController helloController, Language_info language_not_to_draw, Text_item text_item) {
        GraphicsContext gc = helloController.canvas_displaying_the_verses.getGraphicsContext2D();
        gc.clearRect(0, 0, helloController.canvas_displaying_the_verses.getWidth(), helloController.canvas_displaying_the_verses.getHeight());

        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        double polygon_x_pos = return_polygon_middle_position(time_line_pane_data);
        long x_pos_time = pixels_to_nanoseconds(time_line_pane_data, polygon_x_pos - time_line_pane_data.getTime_line_base_line());

        for (Language_info language_info : helloController.list_view_with_all_of_the_languages.getItems()) {
            if (language_info.isVisible_check_mark_checked()) {
                if (language_info.equals(language_not_to_draw)) {
                    place_the_canvas_text(helloController, helloController.canvas_displaying_the_verses, text_item, x_pos_time);
                } else {
                    place_the_canvas_text(helloController, helloController.canvas_displaying_the_verses, language_info.getArrayList_of_all_of_the_translations().get(selected_verse), x_pos_time);
                }
            }
        }
        if (helloController.check_box_saying_help_spread_the_app.isSelected()) {
            Text_item made_with_sabrly_text_item = get_the_made_with_sabrly_text_item(helloController.canvas_displaying_the_verses.getWidth() / 2D, helloController.canvas_displaying_the_verses.getHeight() * 0.95D);
            place_the_canvas_text(helloController, helloController.canvas_displaying_the_verses, made_with_sabrly_text_item, 0);
        }
    }

    private void place_the_canvas_text(HelloController helloController, Canvas canvas, long milliseconds) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int local_selected_verse = which_verse_am_i_on_milliseconds(milliseconds);
        for (Language_info language_info : helloController.list_view_with_all_of_the_languages.getItems()) {
            if (language_info.isVisible_check_mark_checked()) {
                place_the_canvas_text(helloController, canvas, language_info.getArrayList_of_all_of_the_translations().get(local_selected_verse), milliseconds);
            }
        }
        if (helloController.check_box_saying_help_spread_the_app.isSelected()) {
            Text_item made_with_sabrly_text_item = get_the_made_with_sabrly_text_item(canvas.getWidth() / 2D, canvas.getHeight() * 0.95D);
            place_the_canvas_text(helloController, canvas, made_with_sabrly_text_item, 0);
        }
    }

    private void place_the_canvas_text(HelloController helloController, Canvas canvas, Text_item text_item, long current_nano_seconds) {
        String adjusted_verse_text = text_item.getAdjusted_verse_text();
        Point2D point2D_of_the_text = text_item.getText_box_info().getCenter_position();
        javafx.scene.paint.Color color_of_text = text_item.getColor();
        Text_on_canvas_mode text_on_canvas_mode = text_item.getText_on_canvas_mode();
        io.github.humbleui.skija.Font font_for_verse = text_item.getFont();
        Text_accessory_info strokeText = text_item.getStroke_info();
        Text_accessory_info shadow_info = text_item.getShadow_info();
        boolean is_stroke_enabled = strokeText.isIs_the_accessory_on();
        javafx.scene.paint.Color stroke_color = strokeText.getAccessory_color();
        double stroke_weight = strokeText.getAccessory_weight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double weight = shadow_info.getAccessory_weight(); // 0–15
        double text_opacity = return_the_text_opacity(helloController, text_item, current_nano_seconds);
        color_of_text = new javafx.scene.paint.Color(color_of_text.getRed(), color_of_text.getGreen(), color_of_text.getBlue(), color_of_text.getOpacity() * text_opacity);
        Paint paint = new Paint().setAntiAlias(true).setColor(colorToInt(color_of_text));
        stroke_color = new javafx.scene.paint.Color(stroke_color.getRed(), stroke_color.getGreen(), stroke_color.getBlue(), stroke_color.getOpacity() * text_opacity);
        Paint strokePaint = new Paint().setAntiAlias(true).setStroke(true).setStrokeWidth((float) stroke_weight).setColor(colorToInt(stroke_color)).setStrokeJoin(PaintStrokeJoin.ROUND).setStrokeCap(PaintStrokeCap.ROUND);
        Paint shadowPaint;
        double t = 0; // Declare t outside to use it in the loop
        if (shadow_info.isIs_the_accessory_on() && shadow_info.getAccessory_weight() > 0) {
            t = weight / shadow_info.getMax_accessory_weight();
            t = Math.min(1.0, Math.max(0.0, t)); // Clamp safety

            double minAlpha = 0.14;
            double maxAlpha = 0.98;

            // 1. Lower exponent (0.7) makes it reach "strong" levels much earlier
            double alpha = minAlpha + Math.pow(t, 0.7) * (maxAlpha - minAlpha);

            float minSigma = 0.20f;
            float maxSigma = 2.7f;

            // 2. Higher exponent (2.5) keeps it soft early, then sharpens/densifies at the end
            float sigma = (float) (maxSigma - Math.pow(t, 2.5) * (maxSigma - minSigma));

            shadowPaint = new Paint()
                    .setAntiAlias(true)
                    .setColor(colorToInt(new javafx.scene.paint.Color(
                            shadow_info.getAccessory_color().getRed(),
                            shadow_info.getAccessory_color().getGreen(),
                            shadow_info.getAccessory_color().getBlue(),
                            alpha * text_opacity)))
                    .setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, sigma, true));
        } else {
            shadowPaint = new Paint().setAntiAlias(true).setColor(colorToInt(new javafx.scene.paint.Color(0, 0, 0, 0.6 * text_opacity))).setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, 1, true));
        }
        String[] lines = adjusted_verse_text.split("\n");
        TextLine[] array_of_text_lines = new TextLine[lines.length];
        float max_width = 0;
        float height = 0;
        for (int i = 0; i < lines.length; i++) {
            TextLine textLine = TextLine.make(lines[i], font_for_verse);
            array_of_text_lines[i] = textLine;
            max_width = Math.max(max_width, textLine.getWidth());
            height += Math.abs(textLine.getAscent()) + Math.abs(textLine.getDescent()) + Math.abs(textLine.getLeading());
        }
        if (array_of_text_lines.length > 0) {
            height -= array_of_text_lines[array_of_text_lines.length - 1].getLeading();
        }
        max_width += 20; // Making sure that the text stroke doesnt get clipped when creating the image
        height += Global_default_values.height_text_margin;
        ImageInfo imageInfo = new ImageInfo((int) Math.ceil(max_width), (int) Math.ceil(height), ColorType.N32, ColorAlphaType.PREMUL);
        Surface surface = Surface.makeRaster(imageInfo);
        io.github.humbleui.skija.Canvas surface_canvas = surface.getCanvas();
        float local_height = Global_default_values.height_text_margin / 2f;
        for (TextLine textLine : array_of_text_lines) {
            if (shadow_info.isIs_the_accessory_on() && shadow_info.getAccessory_weight() > 0) {
                float x = ((max_width - textLine.getWidth()) / 2F) + 3;
                // 3. Added a dynamic Y-offset (up to 4 pixels) based on weight
                float yShadow = Math.abs(textLine.getAscent()) + local_height + 3 + (float) (t);
                surface_canvas.drawTextLine(textLine, x, yShadow, shadowPaint);
                // 4. Double-pass at high intensity (weight > 8) to make it feel extra heavy
                if (t > 0.8) {
                    surface_canvas.drawTextLine(textLine, x, yShadow, shadowPaint);
                }
            }
            if (is_stroke_enabled && stroke_weight > 0) {
                surface_canvas.drawTextLine(textLine, (max_width - textLine.getWidth()) / 2F, Math.abs(textLine.getAscent()) + local_height, strokePaint);
            }
            surface_canvas.drawTextLine(textLine, (max_width - textLine.getWidth()) / 2F, Math.abs(textLine.getAscent()) + local_height, paint);
            local_height += textLine.getDescent() + Math.abs(textLine.getAscent()) + textLine.getLeading();
        }
        Image fxImage = convertSkijaToFx(surface);
        gc.drawImage(fxImage, point2D_of_the_text.getX() - fxImage.getWidth() / 2D, point2D_of_the_text.getY() - fxImage.getHeight() / 2D);
    }

    private Image convertSkijaToFx(Surface surface) {
        try (io.github.humbleui.skija.Image skImage = surface.makeImageSnapshot();
             Bitmap bitmap = Bitmap.makeFromImage(skImage)) {

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            byte[] pixels = bitmap.readPixels(); // copies into a heap byte[]

            if (cached_text_image == null ||
                    cached_text_image.getWidth() != width ||
                    cached_text_image.getHeight() != height) {
                cached_text_image = new WritableImage(width, height);
            }

            PixelWriter pixelWriter = cached_text_image.getPixelWriter();
            pixelWriter.setPixels(0, 0, width, height,
                    PixelFormat.getByteBgraPreInstance(),
                    pixels, 0,
                    width * 4);

            return cached_text_image;
        }
    }

    private double return_the_text_opacity(HelloController helloController, Text_item text_item, long x_pos_time) {
        if (text_item.getFade_in() == 0 && text_item.getFade_out() == 0) {
            return 1D;
        }
        long time_at_start = ayats_processed.get(selected_verse).getStart_millisecond();
        long time_at_end = ayats_processed.get(selected_verse).getStart_millisecond() + ayats_processed.get(selected_verse).getDuration();
        long time_since_start = x_pos_time - time_at_start;
        long time_till_end = time_at_end - x_pos_time;
        double fade_in_multiplier;
        double fade_out_multiplier;
        if (text_item.getFade_in() == 0) {
            fade_in_multiplier = 1;
        } else {
            fade_in_multiplier = Math.min(1, time_since_start / (text_item.getFade_in() * TimeUnit.SECONDS.toNanos(1)));
            fade_in_multiplier = Math.max(0, fade_in_multiplier);
        }
        if (text_item.getFade_out() == 0) {
            fade_out_multiplier = 1;
        } else {
            fade_out_multiplier = Math.min(1, time_till_end / (text_item.getFade_out() * TimeUnit.SECONDS.toNanos(1)));
            fade_out_multiplier = Math.max(0, fade_out_multiplier);
        }
        return fade_in_multiplier * fade_out_multiplier;
    }

    private DropShadow get_drop_shadow_based_on_intensity(double intensity, javafx.scene.paint.Color color) {
        /*javafx.scene.paint.Color shadow_color = new javafx.scene.paint.Color(color.getRed(),color.getGreen(),color.getBlue(),intensity/10D);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(2.0);              // horizontal offset of the shadow
        dropShadow.setOffsetY(2.0);              // vertical offset
        dropShadow.setRadius(4.0);               // blur radius
        dropShadow.setColor(shadow_color);
        return dropShadow;*/

        double baseOffset = 0.8;   // slightly higher base offset for stronger presence
        double maxOffset = 4.0;    // more noticeable at full intensity
        double baseRadius = 1.5;   // increase for a softer yet stronger spread
        double maxRadius = 8.0;    // larger blur radius
        double spread_plus = 0.08; // stronger minimum spread
        double spread_multiplier = 0.15; // increase spread scaling
        double applied_intensity = Math.min(1D, (intensity / 10D) * 1.5D);
        javafx.scene.paint.Color shadow_color = new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), applied_intensity);
        DropShadow dropShadow = new DropShadow();
        double radius = baseRadius + (intensity / 10.0) * (maxRadius - baseRadius);
        double offset = baseOffset + (intensity / 10.0) * (maxOffset - baseOffset);
        dropShadow.setOffsetX(offset);              // horizontal offset of the shadow
        dropShadow.setOffsetY(offset);              // vertical offset
        dropShadow.setRadius(radius);               // blur radius
        dropShadow.setSpread(spread_plus + spread_multiplier * (intensity / 10.0)); // optional subtle spread scaling
        dropShadow.setColor(shadow_color);
        return dropShadow;
    }

    private void place_the_box_surrounding_the_text(HelloController helloController, Language_info language_info) {
        Text_item text_item = language_info.getArrayList_of_all_of_the_translations().get(selected_verse);
        Text_box_info text_box_info = text_item.getText_box_info();
        Point2D point2D_of_the_text = text_box_info.getCenter_position();
        GraphicsContext gc = helloController.canvas_displaying_the_verses.getGraphicsContext2D();
        double width = text_box_info.getText_box_width();
        double height = text_box_info.getText_box_height();
        double rectangle_width = get_stroke_width_for_canvas(gc,5);
        gc.save();
        gc.setImageSmoothing(false);
        gc.setFill(javafx.scene.paint.Color.TRANSPARENT);
        gc.setStroke(javafx.scene.paint.Color.RED);
        gc.setLineWidth(rectangle_width);
        gc.fillRect(point2D_of_the_text.getX() - width / 2, point2D_of_the_text.getY() - height / 2, width, height);
        gc.strokeRect(point2D_of_the_text.getX() - width / 2, point2D_of_the_text.getY() - height / 2, width, height);
        gc.restore();
    }

    private double get_stroke_width_for_canvas(GraphicsContext gc, double logical_pixels) {
        Affine t = gc.getTransform();
        double scale = Math.sqrt(t.getMxx() * t.getMxx() + t.getMyx() * t.getMyx());
        return logical_pixels / scale;
    }

    private void place_the_x_and_y_text_positions(Canvas canvas, Text_item text_item) {
        final int x_padding = 20;
        final int y_padding = 20;
        final double font_size = 36;
        int x_position = (int) text_item.getText_box_info().getCenter_position().getX();
        int y_position = (int) text_item.getText_box_info().getCenter_position().getY();
        Text x_and_y_positions = new Text("x: " + x_position + " y: " + y_position);
        x_and_y_positions.setFont(new Font(font_size));
        double total_rectangle_width = x_and_y_positions.getLayoutBounds().getWidth() + x_padding;
        double total_rectangle_height = x_and_y_positions.getLayoutBounds().getHeight() + y_padding;
        double x_canvas_position = (canvas.getWidth() / 2D) - total_rectangle_width / 2D;
        double y_canvas_position;
        if (y_position <= Global_default_values.translation_canvas_height * 0.55) {
            y_canvas_position = (canvas.getHeight() * 0.75D) + total_rectangle_height / 2D;
        } else {
            y_canvas_position = (canvas.getHeight() * 0.25D) + total_rectangle_height / 2D;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(new javafx.scene.paint.Color(0.5, 0.5, 0.5, 0.925));
        gc.fillRoundRect(x_canvas_position, y_canvas_position, total_rectangle_width, total_rectangle_height, 20, 20);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(new Font(font_size));
        gc.fillText(x_and_y_positions.getText(), x_canvas_position + x_padding / 2D, y_canvas_position + x_and_y_positions.getBaselineOffset() + y_padding / 2D);
    }

    public static int colorToInt(javafx.scene.paint.Color color) {
        // There is a bug in the skia library in terms that it accepts abgr instead of argb
        int a = (int) Math.round(color.getOpacity() * 255);
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    /*private void set_the_width_and_height_of_text_box(Text_item text_item) {
        String adjusted_verse_text = text_item.getAdjusted_verse_text();
        Point2D point2D_of_the_text = text_item.getPoint2D();
        Font font_for_verse = text_item.getFont();
        Text text = new Text(adjusted_verse_text);
        text.setFont(font_for_verse);
        double width = text.getLayoutBounds().getWidth();
        double height = text.getLayoutBounds().getHeight();
        Text_box_info text_box_info = text_item.getText_box_info();
        if (!text_box_info.isSet()) {
            text_box_info = new Text_box_info(point2D_of_the_text, width, height, true);
            text_item.setText_box_info(text_box_info);
        } else {
            if (width > text_box_info.getText_box_width()) {
                text_box_info.setText_box_width(width);
            }
            if (height > text_box_info.getText_box_height()) {
                text_box_info.setText_box_height(height);
            }
        }
    }*/

    private void bind_the_canvas_to_the_image_view(HelloController helloController, Canvas canvas) {
        DoubleBinding x_binding = new DoubleBinding() {
            {
                super.bind(helloController.chatgpt_image_view.fitWidthProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.chatgpt_image_view.getFitWidth() / canvas.getWidth();
            }
        };
        DoubleBinding x_translate_binding = new DoubleBinding() {
            {
                super.bind(x_binding);
            }

            @Override
            protected double computeValue() {
                return -(canvas.getWidth() - canvas.getScaleX() * canvas.getWidth()) / 2;
            }
        };
        DoubleBinding y_binding = new DoubleBinding() {
            {
                super.bind(helloController.chatgpt_image_view.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return helloController.chatgpt_image_view.getFitHeight() / canvas.getHeight();
            }
        };
        DoubleBinding y_translate_binding = new DoubleBinding() {
            {
                super.bind(y_binding);
            }

            @Override
            protected double computeValue() {
                return -(canvas.getHeight() - canvas.getScaleY() * canvas.getHeight()) / 2;
            }
        };
        canvas.scaleXProperty().bind(x_binding);
        canvas.scaleYProperty().bind(y_binding);
        canvas.translateXProperty().bind(x_translate_binding);
        canvas.translateYProperty().bind(y_translate_binding);
    }

    private void add_style_sheet_to_the_scene(Scene scene, String css_file_path) {
        URL css_url = getClass().getResource(css_file_path);
        if (css_url == null) {
            return;
        }
        scene.getStylesheets().add(
                css_url.toExternalForm()
        );
    }

    private void add_the_css_files_at_the_start(Scene scene) {
        add_style_sheet_to_the_scene(scene, "my-separator.css");
        add_style_sheet_to_the_scene(scene, "toggle_switch_style.css");
        add_style_sheet_to_the_scene(scene, "verse-text-area.css");
    }

    private void format_the_text_filed_to_only_accept_positive_integers(TextField textField) {
        int max_int_length = String.valueOf(Integer.MAX_VALUE).length();
        UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return change;
                }
                if (newText.matches("\\d+") && newText.length() <= max_int_length - 1) {
                    return change;
                } else {
                    return null; // block anything else
                }
            }
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    private void bind_the_root_to_list_view(HelloController helloController, Region root, ObjectProperty<Insets> padding_property) {
        ScrollBar scrollBar = null;
        Set<Node> set_of_scroll_bars = helloController.list_view_with_all_of_the_languages.lookupAll(".scroll-bar:vertical");
        for (Node node : set_of_scroll_bars) {
            if (scrollBar == null) {
                scrollBar = (ScrollBar) node;
            } else {
                if (((ScrollBar) node).getWidth() > scrollBar.getWidth()) {
                    scrollBar = (ScrollBar) node;
                }
            }
        } // TODO this code needs to be looked at
        ScrollBar finalScrollBar = scrollBar;
        DoubleBinding double_binding = new DoubleBinding() {
            {
                super.bind(helloController.list_view_with_all_of_the_languages.widthProperty(), finalScrollBar.widthProperty(), padding_property);
            }

            @Override
            protected double computeValue() {
                double left_padding = 0;
                double right_padding = 0;
                Insets insets = padding_property.get();
                if (insets != null) {
                    left_padding = insets.getLeft();
                    right_padding = insets.getRight();
                }
                return helloController.list_view_with_all_of_the_languages.widthProperty().get() /*- finalScrollBar.widthProperty().get()*/ - 15 - left_padding - right_padding - list_view_languages_border_width * 2; // TODO its very important to look at the width of scroll bar.
            }
        };
        root.minWidthProperty().bind(double_binding);
        root.prefWidthProperty().bind(double_binding);
        root.maxWidthProperty().bind(double_binding);
    }

    private void set_the_border_width_of_list_view_languages(HelloController helloController) {
        String new_style = helloController.list_view_with_all_of_the_languages.getStyle() + "-fx-border-width: " + list_view_languages_border_width;
        helloController.list_view_with_all_of_the_languages.setStyle(new_style);
    }

    /*private void bind_the_languages_list_view_to_the_right_border_pane(HelloController helloController){
        helloController.list_view_with_all_of_the_languages.minWidthProperty().bind(helloController.right_stack_pane_in_grid_pane.widthProperty().subtract(20));
        helloController.list_view_with_all_of_the_languages.prefWidthProperty().bind(helloController.right_stack_pane_in_grid_pane.widthProperty().subtract(20));
        helloController.list_view_with_all_of_the_languages.maxWidthProperty().bind(helloController.right_stack_pane_in_grid_pane.widthProperty().subtract(20));
    }*/

    private void bind_an_item_to_a_property(Region child, ObservableDoubleValue observableValue, double substracted_value) {
        child.minWidthProperty().bind(Bindings.subtract(observableValue, substracted_value));
        child.prefWidthProperty().bind(Bindings.subtract(observableValue, substracted_value));
        child.maxWidthProperty().bind(Bindings.subtract(observableValue, substracted_value));
    }

    private String edit_the_verses_before_adding_them(String verse_text) {
        Document doc = Jsoup.parse(verse_text);
        doc.select("script, style, noscript, sup, sub").remove(); // drop entire elements incl. text
        String cleaned_html = doc.text().trim();
        cleaned_html = cleaned_html.replaceAll("::\\{\\d+}", "");
        cleaned_html = cleaned_html.replace("\"", "");
        return cleaned_html;
    }

    private ArrayList<Text_item> return_the_formatted_text_item_from_array_list(ArrayList<String> arrayList_of_strings) {
        ArrayList<Text_item> array_list_to_be_returned = new ArrayList<>(arrayList_of_strings.size());
        for (int i = 0; i < arrayList_of_strings.size(); i++) {
            Text_item text_item = new Text_item(edit_the_verses_before_adding_them(arrayList_of_strings.get(i)));
            array_list_to_be_returned.add(text_item);
        }
        return array_list_to_be_returned;
    }

    private void add_the_fonts_to_the_combo_box(ComboBox<String> comboBox) {
        FontMgr font_manager = FontMgr.getDefault();
        ArrayList<String> array_list_of_families = new ArrayList<>();
        for (int i = 0; i < font_manager.getFamiliesCount(); i++) {
            array_list_of_families.add(font_manager.getFamilyName(i));
        }
        comboBox.getItems().addAll(array_list_of_families);
    }

    private void add_all_sub_fonts_to_sub_fonts_combo_box(ComboBox<String> combo_box_with_all_fonts, ComboBox<Sub_font_name_and_style> combox_of_all_of_fonts_sub_choices) {
        combox_of_all_of_fonts_sub_choices.getItems().clear();
        FontStyleSet font_style_set = FontMgr.getDefault().matchFamily(combo_box_with_all_fonts.getSelectionModel().getSelectedItem());
        if (font_style_set.count() == 0) {
            combox_of_all_of_fonts_sub_choices.getItems().add(new Sub_font_name_and_style("Regular", FontStyle.NORMAL));
        } else {
            for (int i = 0; i < font_style_set.count(); i++) {
                combox_of_all_of_fonts_sub_choices.getItems().add(new Sub_font_name_and_style(font_style_set.getStyleName(i), font_style_set.getStyle(i)));
            }
        }
    }

    private javafx.scene.shape.Path makeSquirclePath(int samples, double n) {
        javafx.scene.shape.Path path = new javafx.scene.shape.Path();
        // Path is defined in a normalized 2x2 box so Region#setScaleShape(true) can scale it
        for (int i = 0; i <= samples; i++) {
            double t = 2.0 * Math.PI * i / samples;
            double c = Math.cos(t), s = Math.sin(t);
            double x = Math.copySign(Math.pow(Math.abs(c), 2.0 / n), c);
            double y = Math.copySign(Math.pow(Math.abs(s), 2.0 / n), s);

            if (i == 0) path.getElements().add(new MoveTo(x, y));
            else path.getElements().add(new LineTo(x, y));
        }
        path.getElements().add(new ClosePath());
        return path;
    }

    private javafx.scene.shape.Path return_default_squircle() {
        return makeSquirclePath(400, 4.0);
    }

    private void listen_to_fast_rewind_button_click(HelloController helloController) { // TODO make these buttons become disabled when it amkes sense for them to be so(start,end etc....)
        helloController.fast_rewind_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                long new_time = TimeUnit.MILLISECONDS.toNanos((long) mediaPlayer.getCurrentTime().toMillis()) - TimeUnit.MINUTES.toNanos(1);
                new_time = make_sure_time_does_not_exceed_min_max(new_time);
                update_the_verse_based_on_milliseconds(helloController, new_time);
                media_has_been_rewinded_or_forwaded(helloController, new_time);
                //helloController.list_view_with_all_of_the_languages.refresh();
                loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
                set_up_everything_image_view_time_line_time(helloController, new_time, Type_of_Image.FULL_QUALITY);
                send_analytics_event("fast_rewind");
            }
        });
    }

    private void set_rewind_button_click(HelloController helloController) {
        helloController.rewind_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (selected_verse > 0) {
                    selected_verse--;
                    the_verse_changed(helloController, selected_verse);
                    scroll_to_specific_verse_time(helloController);
                    //helloController.list_view_with_all_of_the_languages.refresh();
                    loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
                    set_up_everything_image_view_time_line_time(helloController, ayats_processed.get(selected_verse).getStart_millisecond(), Type_of_Image.FULL_QUALITY);
                    send_analytics_event("rewind");
                }
            }
        });
    }

    private void play_pause_button_click_listen(HelloController helloController) {
        helloController.play_pause_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                play_or_pause_the_video_after_click(helloController);
                send_analytics_event("play/pause");
            }
        });
    }

    private void listen_to_forward_button_click(HelloController helloController) {
        helloController.forward_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (selected_verse < ayats_processed.size() - 1) {
                    selected_verse++;
                    the_verse_changed(helloController, selected_verse);
                    scroll_to_specific_verse_time(helloController);
                    //helloController.list_view_with_all_of_the_languages.refresh();
                    loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
                    set_up_everything_image_view_time_line_time(helloController, ayats_processed.get(selected_verse).getStart_millisecond(), Type_of_Image.FULL_QUALITY);
                    send_analytics_event("forward");
                }
            }
        });
    }

    private void fast_forward_button_click(HelloController helloController) {
        helloController.fast_forward_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) { // TODO make these buttons become disabled when it amkes sense for them to be so(start,end etc....)
                long new_time = TimeUnit.MILLISECONDS.toNanos((long) mediaPlayer.getCurrentTime().toMillis()) + TimeUnit.MINUTES.toNanos(1);
                new_time = make_sure_time_does_not_exceed_min_max(new_time);
                update_the_verse_based_on_milliseconds(helloController, new_time);
                media_has_been_rewinded_or_forwaded(helloController, new_time);
                //helloController.list_view_with_all_of_the_languages.refresh();
                loop_through_all_verses_and_update(helloController, helloController.list_view_with_all_of_the_languages);
                set_up_everything_image_view_time_line_time(helloController, new_time, Type_of_Image.FULL_QUALITY);
                send_analytics_event("fast_forward");
            }
        });
    }

    private void media_has_been_rewinded_or_forwaded(HelloController helloController, long time_in_nanoSeconds) {
        Polygon polygon = get_the_polygon_from_the_time_line_over_lay(helloController);
        Polygon_data polygon_data = (Polygon_data) polygon.getUserData();
        if (!polygon_data.isShould_the_polygon_be_fixed_in_the_middle()) {
            set_the_scroll_pane_h_value_auto_scroll(helloController, return_the_real_x_position_based_on_time(helloController, time_in_nanoSeconds));
            update_the_time_line_indicator(helloController, time_in_nanoSeconds);
        }
        mediaPlayer.seek(Duration.millis(TimeUnit.NANOSECONDS.toMillis(time_in_nanoSeconds)));
        lastKnownSystemTime = 0;
    }

    private long make_sure_time_does_not_exceed_min_max(long time_in_nanoSeconds) {
        time_in_nanoSeconds = Math.max(time_in_nanoSeconds, 0);
        time_in_nanoSeconds = Math.min(time_in_nanoSeconds, get_duration());
        return time_in_nanoSeconds;
    }

    private String remove_trailing_zeroes_from_number(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.format("%s", number);
        }
    }

    private void bind_question_mark_beside_check_box_image(HelloController helloController) {
        final double add_width_and_height_to_button_size = 5;
        final int minus_from_logo_button = 4;
        helloController.question_mark_beside_help_spread_the_app.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                helloController.question_mark_beside_help_spread_the_app.setGraphic(return_the_icon("question_mark", new_number.intValue() - minus_from_logo_button, new_number.intValue() - minus_from_logo_button));
            }
        });
        helloController.question_mark_beside_help_spread_the_app.minHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.question_mark_beside_help_spread_the_app.prefHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.question_mark_beside_help_spread_the_app.maxHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.question_mark_beside_help_spread_the_app.minWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.question_mark_beside_help_spread_the_app.prefWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.question_mark_beside_help_spread_the_app.maxWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
    }

    private void set_up_help_spread_app_learn_more_button(HelloController helloController) {
        helloController.question_mark_beside_help_spread_the_app.setShape(return_default_squircle());
        helloController.question_mark_beside_help_spread_the_app.setGraphic(return_the_icon("question_mark", 20, 20));
        helloController.question_mark_beside_help_spread_the_app.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.question_mark_beside_help_spread_the_app.setAlignment(Pos.CENTER);
    }

    private void listen_to_learn_more_spread_app_button(HelloController helloController) {
        helloController.question_mark_beside_help_spread_the_app.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                learn_more_about_spreading_the_app_dialog();
                send_analytics_event("Help spread sabrly info used");
            }
        });
    }

    private void learn_more_about_spreading_the_app_dialog() { // TODO change how this dialog looks
        if (learn_more_about_app_stage != null && learn_more_about_app_stage.isShowing()) {
            learn_more_about_app_stage.toFront();
            return;
        }
        learn_more_about_app_stage = new Stage();
        learn_more_about_app_stage.initOwner(main_stage);
        learn_more_about_app_stage.initStyle(StageStyle.DECORATED);
        //feedbackStage.initModality(Modality.WINDOW_MODAL); // optional
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label main_text = new Label("If you’d like, you can leave a small " + app_name + " watermark on your video. It’s not required, but it will help spread " + app_name + " and it would be a source of ajr and sadaqah jariyah for you.");
        JFXButton got_it_button = new JFXButton("Got it");
        got_it_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                learn_more_about_app_stage.close();
            }
        });

        VBox.setMargin(main_text, new Insets(0, 10, 0, 10));
        VBox.setMargin(got_it_button, new Insets(25, 10, 0, 10));
        main_text.setAlignment(Pos.CENTER);
        main_text.setTextAlignment(TextAlignment.CENTER); // center each line of text
        main_text.setWrapText(true);
        got_it_button.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
        got_it_button.setFocusTraversable(false);
        vBox.getChildren().addAll(main_text, got_it_button);
        Scene scene = new Scene(vBox, 450, 225);
        learn_more_about_app_stage.setScene(scene);
        learn_more_about_app_stage.setTitle("Help spread " + app_name);
        learn_more_about_app_stage.show();
    }


    private void bind_the_opacaity_canvas_to_image_view(HelloController helloController) {
        helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.heightProperty().bind(helloController.chatgpt_image_view.fitHeightProperty());
        helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.widthProperty().bind(helloController.chatgpt_image_view.fitWidthProperty());
        /*helloController.black_rectangle_behind_image_view.heightProperty().bind(helloController.chatgpt_image_view.fitHeightProperty());
        helloController.black_rectangle_behind_image_view.widthProperty().bind(helloController.chatgpt_image_view.fitWidthProperty());*/
    }

    private void set_the_width_and_color_of_image_control_stack_pane(HelloController helloController) {
        helloController.image_controls_stack_pane.setStyle("-fx-border-color: #bcbcbc; -fx-border-width: 0.5;");
    }

    private void listen_to_help_app_spread_check_mark(HelloController helloController) {
        helloController.check_box_saying_help_spread_the_app.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_state, Boolean new_state) {
                place_the_canvas_text(helloController);
                send_analytics_event("help_spread_sabrly_clicked");
            }
        });
    }

    /*private void enable_or_disable_the_image_control_menu(HelloController helloController, Shape_object_time_line shape_object_found) {
        if (shape_object_found == null) {
            helloController.image_controls_stack_pane.setDisable(true);
            helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(0);
            helloController.slider_to_control_the_opacity_of_an_image.setValue(100);
            helloController.slider_to_control_fade_in_of_image.setValue(0);
            helloController.slider_to_control_fade_out_of_image.setValue(0);
            helloController.label_holding_the_opacity_percentage.setText("100" + unit_sign_beside_opacity);
            helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_in_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);
            helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_out_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);
        } else {
            helloController.image_controls_stack_pane.setDisable(false);
            helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(return_opacaity_in_zero_to_one_format(shape_object_found.getOpacity()));
            helloController.slider_to_control_the_opacity_of_an_image.setValue(shape_object_found.getOpacity());
            helloController.slider_to_control_fade_in_of_image.setValue(shape_object_found.getFade_in());
            helloController.slider_to_control_fade_out_of_image.setValue(shape_object_found.getFade_out());
            helloController.label_holding_the_opacity_percentage.setText((int) shape_object_found.getOpacity() + unit_sign_beside_opacity);
            helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(shape_object_found.getFade_in()) + unit_sign_beside_fade_in_fade_out);
            helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(shape_object_found.getFade_out()) + unit_sign_beside_fade_in_fade_out);
        }
    }*/

    private double return_opacaity_in_zero_to_one_format(double value) {
        double percent = value / 100D;
        return 1 - percent;
    }


    private int return_percentage_based_on_value(double value, double max_value) {
        return (int) ((100 * value) / max_value);
    }

    private String return_formatted_string_to_1_decimal_place_always(double value) {
        return String.format("%.1f", value);
    }

    private String find_the_widest_text(double min, double max, double increment) {
        Text test_to_find_widest_text = new Text(return_formatted_string_to_1_decimal_place_always(min));
        double widest_width = test_to_find_widest_text.getLayoutBounds().getWidth();
        String widest_text = return_formatted_string_to_1_decimal_place_always(min);
        for (double i = min; i <= max; i += increment) {
            test_to_find_widest_text.setText(return_formatted_string_to_1_decimal_place_always(i));
            if (test_to_find_widest_text.getLayoutBounds().getWidth() > widest_width) {
                widest_text = return_formatted_string_to_1_decimal_place_always(i);
                widest_width = test_to_find_widest_text.getLayoutBounds().getWidth();
            }
        }
        return widest_text;
    }

    private String return_the_widest_text_for_opacity(int min_percentage, int max_percentage, int percentage_increment) {
        Text text_to_measure_length = new Text(String.valueOf(min_percentage) + unit_sign_beside_opacity);
        double max_width = text_to_measure_length.getLayoutBounds().getWidth();
        String max_percentage_width = String.valueOf(min_percentage) + unit_sign_beside_opacity;
        for (int i = min_percentage; i <= max_percentage; i += percentage_increment) {
            text_to_measure_length.setText(String.valueOf(i) + unit_sign_beside_opacity);
            if (text_to_measure_length.getLayoutBounds().getWidth() > max_width) {
                max_percentage_width = String.valueOf(i) + unit_sign_beside_opacity;
                max_width = text_to_measure_length.getLayoutBounds().getWidth();
            }
        }
        return max_percentage_width;
    }

    private String return_the_widest_text_for_fade_in_and_fade_out(double min_time, double max_time, double increment) {
        Text text_to_measure_length = new Text(String.valueOf(min_time) + unit_sign_beside_fade_in_fade_out);
        double max_width = text_to_measure_length.getLayoutBounds().getWidth();
        String max_percentage_width = return_formatted_string_to_1_decimal_place_always(min_time) + unit_sign_beside_fade_in_fade_out;
        for (double i = min_time; i <= max_time; i += increment) {
            text_to_measure_length.setText(return_formatted_string_to_1_decimal_place_always(i) + unit_sign_beside_fade_in_fade_out);
            if (text_to_measure_length.getLayoutBounds().getWidth() > max_width) {
                max_percentage_width = return_formatted_string_to_1_decimal_place_always(i) + unit_sign_beside_fade_in_fade_out;
                max_width = text_to_measure_length.getLayoutBounds().getWidth();
            }
        }
        return max_percentage_width;
    }

    private void set_the_fake_width_opacity(HelloController helloController) {
        String widest_text = return_the_widest_text_for_opacity(0, 100, 1);
        helloController.fake_label_holding_the_opacity_percentage.setText(widest_text);
    }

    private void set_the_fake_width_in_image_controls(HelloController helloController) {
        String widest_text = return_the_widest_text_for_fade_in_and_fade_out(0, 2, 0.1);
        helloController.fake_label_holding_the_fade_in.setText(widest_text);
        helloController.fake_label_holding_the_fade_out.setText(widest_text);
    }

    private void set_the_opacity_initially(HelloController helloController) {
        helloController.label_holding_the_opacity_percentage.setText(((int) helloController.slider_to_control_the_opacity_of_an_image.getMax()) + unit_sign_beside_opacity);
    }

    private void set_the_fade_in_fade_out_initially(HelloController helloController) {
        String min_fade_out = return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_out_of_image.getMin());
        helloController.label_holding_the_fade_out.setText(min_fade_out + unit_sign_beside_fade_in_fade_out);
    }

    private void set_the_fade_in_fade_in_initially(HelloController helloController) {
        String min_fade_out = return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_in_of_image.getMin());
        helloController.label_holding_the_fade_in.setText(min_fade_out + unit_sign_beside_fade_in_fade_out);
    }

    private void listen_to_mouse_clicked_inside_image_view_pane(HelloController helloController) {
        final Text_on_canvas_dragged[] text_on_canvas_dragged = new Text_on_canvas_dragged[1];
        helloController.stack_pane_of_image_view_and_text.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ObservableList<Language_info> all_of_the_languages = helloController.list_view_with_all_of_the_languages.getItems();
                Smallest_text_box_info smallest_text_box_info = return_the_smallest_text_box_info(helloController, all_of_the_languages, mouseEvent);
                Type_of_cursor type_of_cursor;
                if (smallest_text_box_info.isSet()) {
                    type_of_cursor = return_type_of_cursor_based_on_position(helloController, mouseEvent, smallest_text_box_info);
                } else {
                    type_of_cursor = Type_of_cursor.DEFAULT;
                }
                set_the_holding_cursor_of_image_view_for_text(helloController, type_of_cursor);
            }
        });

        helloController.stack_pane_of_image_view_and_text.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                send_analytics_event("verse_text_position_updated");
                place_the_canvas_text(helloController);
                set_all_the_languages_selected_set_to_false(helloController);
                ObservableList<Language_info> all_of_the_languages = helloController.list_view_with_all_of_the_languages.getItems();
                Smallest_text_box_info smallest_text_box_info = return_the_smallest_text_box_info(helloController, all_of_the_languages, mouseEvent);
                if (smallest_text_box_info.isSet()) {
                    place_the_box_surrounding_the_text(helloController, smallest_text_box_info.getLanguage_info());
                    smallest_text_box_info.getLanguage_info().setText_box_showing(true);
                    double x_scale = helloController.canvas_displaying_the_verses.getScaleX();
                    double y_scale = helloController.canvas_displaying_the_verses.getScaleY();
                    Type_of_cursor type_of_cursor = return_type_of_cursor_based_on_position(helloController, mouseEvent, smallest_text_box_info);
                    int index = helloController.list_view_with_all_of_the_languages.getItems().indexOf(smallest_text_box_info.getLanguage_info());
                    text_on_canvas_dragged[0] = new Text_on_canvas_dragged(new Point2D(mouseEvent.getX(), mouseEvent.getY()), new Point2D(smallest_text_box_info.getLanguage_info().getArrayList_of_all_of_the_translations().get(selected_verse).getText_box_info().getCenter_position().getX(), smallest_text_box_info.getLanguage_info().getArrayList_of_all_of_the_translations().get(selected_verse).getText_box_info().getCenter_position().getY()), smallest_text_box_info.getLanguage_info(), type_of_cursor, smallest_text_box_info.getLanguage_info().getArrayList_of_all_of_the_translations().get(selected_verse), smallest_text_box_info.getText_box_info().getText_box_height(), smallest_text_box_info.getText_box_info().getText_box_width(), Global_default_values.translation_canvas_height, Global_default_values.translation_canvas_width, y_scale, x_scale, index, helloController.canvas_holding_the_text_position);
                    switch (type_of_cursor) {
                        case CENTER -> {
                            helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.CLOSED_HAND);
                        }
                        case NORTH -> {
                            text_on_canvas_dragged[0].setHeight_difference(mouseEvent.getY() - smallest_text_box_info.getText_box_info().getMin_y_point() * y_scale);
                        }
                        case EAST -> {
                            text_on_canvas_dragged[0].setWidth_difference(smallest_text_box_info.getText_box_info().getMax_x_point() * x_scale - mouseEvent.getX());
                        }
                        case SOUTH -> {
                            text_on_canvas_dragged[0].setHeight_difference(smallest_text_box_info.getText_box_info().getMax_y_point() * y_scale - mouseEvent.getY());
                        }
                        case WEST -> {
                            text_on_canvas_dragged[0].setWidth_difference(mouseEvent.getX() - smallest_text_box_info.getText_box_info().getMin_x_point() * x_scale);
                        }
                    }
                }
            }
        });
        helloController.stack_pane_of_image_view_and_text.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (text_on_canvas_dragged[0] != null) {
                    if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.CENTER) {
                        Text_item text_item = text_on_canvas_dragged[0].getText_item();
                        Text_box_info text_box_info = text_item.getText_box_info();
                        double x_pos_difference = mouseEvent.getX() - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getX();
                        double y_pos_difference = mouseEvent.getY() - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getY();
                        x_pos_difference = x_pos_difference / text_on_canvas_dragged[0].getX_scale();
                        y_pos_difference = y_pos_difference / text_on_canvas_dragged[0].getY_scale();
                        double new_x_position = text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX() + x_pos_difference;
                        double new_y_position = text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY() + y_pos_difference;
                        new_x_position = Math.max(text_box_info.getText_box_width() / 2D, new_x_position);
                        new_y_position = Math.max(text_box_info.getText_box_height() / 2D, new_y_position);
                        new_x_position = Math.min(new_x_position, text_on_canvas_dragged[0].getCanvas_width() - text_box_info.getText_box_width() / 2D);
                        new_y_position = Math.min(new_y_position, text_on_canvas_dragged[0].getCanvas_height() - text_box_info.getText_box_height() / 2D);
                        text_item.getText_box_info().setCenter_position(new Point2D(new_x_position, new_y_position));
                        place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                        place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                        place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.NORTH) {
                        Text_item text_item = text_on_canvas_dragged[0].getText_item();
                        Text_box_info text_box_info = text_item.getText_box_info();
                        double mouse_y_position = Math.max(mouseEvent.getY(), text_on_canvas_dragged[0].getHeight_difference());
                        double y_pos_difference = mouse_y_position - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getY();
                        y_pos_difference = y_pos_difference / text_on_canvas_dragged[0].getY_scale();
                        double new_height = text_on_canvas_dragged[0].getOriginal_height() - y_pos_difference;
                        if (new_height >= text_box_info.getMin_height()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX(), text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY() + y_pos_difference / 2D));
                            text_box_info.setText_box_height(text_on_canvas_dragged[0].getOriginal_height() - y_pos_difference);
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        } else if (new_height != text_box_info.getMin_height()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX(), text_on_canvas_dragged[0].get_max_y_of_text() - text_box_info.getMin_height() / 2D));
                            text_box_info.setText_box_height(text_box_info.getMin_height());
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        }
                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.EAST) {
                        Text_item text_item = text_on_canvas_dragged[0].getText_item();
                        Text_box_info text_box_info = text_item.getText_box_info();
                        double mouse_x_position = Math.min(mouseEvent.getX(), text_on_canvas_dragged[0].getCanvas_width() * text_on_canvas_dragged[0].getX_scale() - text_on_canvas_dragged[0].getWidth_difference());
                        double x_pos_difference = mouse_x_position - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getX();
                        x_pos_difference = x_pos_difference / text_on_canvas_dragged[0].getX_scale();
                        double new_width = text_on_canvas_dragged[0].getOriginal_width() + x_pos_difference;
                        String adjusted_verse = Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item.getVerse_text(), text_item.getFont(), Math.max(new_width, text_box_info.getMin_width()) - text_item.getExtra_width_padding(), text_item.getLeft_margin(), text_item.getRight_margin());
                        double[] width_and_height_of_adjusted_text = Text_sizing.getInstance().get_width_and_height_of_string(adjusted_verse, text_item.getFont());
                        double new_text_height = Math.max(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding(), text_on_canvas_dragged[0].getOriginal_height());
                        text_item.setAdjusted_verse_text(adjusted_verse);
                        if (new_width >= text_box_info.getMin_width()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX() + x_pos_difference / 2D, text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY()));
                            text_box_info.setText_box_width(new_width);
                            text_box_info.setText_box_height(new_text_height);
                            text_box_info.setMin_height(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding());
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        } else if (new_width != text_box_info.getMin_width()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].get_min_x_of_text() + text_box_info.getMin_width() / 2D, text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY()));
                            text_box_info.setText_box_width(text_box_info.getMin_width());
                            text_box_info.setText_box_height(new_text_height);
                            text_box_info.setMin_height(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding());
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        }
                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.SOUTH) {
                        Text_item text_item = text_on_canvas_dragged[0].getText_item();
                        Text_box_info text_box_info = text_item.getText_box_info();
                        double mouse_y_position = Math.min(mouseEvent.getY(), text_on_canvas_dragged[0].getCanvas_height() * text_on_canvas_dragged[0].getY_scale() - text_on_canvas_dragged[0].getHeight_difference());
                        double y_pos_difference = mouse_y_position - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getY();
                        y_pos_difference = y_pos_difference / text_on_canvas_dragged[0].getY_scale();
                        double new_height = text_on_canvas_dragged[0].getOriginal_height() + y_pos_difference;
                        if (new_height >= text_box_info.getMin_height()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX(), text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY() + y_pos_difference / 2D));
                            text_box_info.setText_box_height(text_on_canvas_dragged[0].getOriginal_height() + y_pos_difference);
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        } else if (new_height != text_box_info.getMin_height()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX(), text_on_canvas_dragged[0].get_min_y_of_text() + text_box_info.getMin_height() / 2D));
                            text_box_info.setText_box_height(text_box_info.getMin_height());
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        }
                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.WEST) {
                        Text_item text_item = text_on_canvas_dragged[0].getText_item();
                        Text_box_info text_box_info = text_item.getText_box_info();
                        double mouse_x_position = Math.max(mouseEvent.getX(), text_on_canvas_dragged[0].getWidth_difference());
                        double x_pos_difference = mouse_x_position - text_on_canvas_dragged[0].getOriginal_point2D_of_mouse_event().getX();
                        x_pos_difference = x_pos_difference / text_on_canvas_dragged[0].getX_scale();
                        double new_width = text_on_canvas_dragged[0].getOriginal_width() - x_pos_difference;
                        String adjusted_verse = Text_sizing.getInstance().do_i_need_to_resize_the_verse_text(text_item.getVerse_text(), text_item.getFont(), Math.max(new_width, text_box_info.getMin_width()) - text_item.getExtra_width_padding(), text_item.getLeft_margin(), text_item.getRight_margin());
                        double[] width_and_height_of_adjusted_text = Text_sizing.getInstance().get_width_and_height_of_string(adjusted_verse, text_item.getFont());
                        double new_text_height = Math.max(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding(), text_on_canvas_dragged[0].getOriginal_height());
                        text_item.setAdjusted_verse_text(adjusted_verse);
                        if (new_width >= text_box_info.getMin_width()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].getOriginal_point2D_of_text().getX() + x_pos_difference / 2D, text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY()));
                            text_box_info.setText_box_width(new_width);
                            text_box_info.setText_box_height(new_text_height);
                            text_box_info.setMin_height(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding());
                            place_the_canvas_text(helloController);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        } else if (new_width != text_box_info.getMin_width()) {
                            text_box_info.setCenter_position(new Point2D(text_on_canvas_dragged[0].get_max_x_of_text() - text_box_info.getMin_width() / 2D, text_on_canvas_dragged[0].getOriginal_point2D_of_text().getY()));
                            text_box_info.setText_box_width(text_box_info.getMin_width());
                            text_box_info.setText_box_height(new_text_height);
                            text_box_info.setMin_height(width_and_height_of_adjusted_text[1] + text_item.getExtra_height_padding());
                            place_the_canvas_text(helloController, text_on_canvas_dragged[0].getLanguage_info(), text_item);
                            place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                            place_the_x_and_y_text_positions(text_on_canvas_dragged[0].getText_position_canvas(), text_item);
                        }
                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.NORTH_EAST) {

                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.SOUTH_EAST) {

                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.SOUTH_WEST) {

                    } else if (text_on_canvas_dragged[0].getType_of_cursor() == Type_of_cursor.NORTH_WEST) {

                    }
                }
            }
        });
        helloController.stack_pane_of_image_view_and_text.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ObservableList<Language_info> all_of_the_languages = helloController.list_view_with_all_of_the_languages.getItems();
                Smallest_text_box_info smallest_text_box_info = return_the_smallest_text_box_info(helloController, all_of_the_languages, mouseEvent);
                Type_of_cursor type_of_cursor;
                if (smallest_text_box_info.isSet()) {
                    type_of_cursor = return_type_of_cursor_based_on_position(helloController, mouseEvent, smallest_text_box_info);
                } else {
                    type_of_cursor = Type_of_cursor.DEFAULT;
                }
                set_the_holding_cursor_of_image_view_for_text(helloController, type_of_cursor);
                if (text_on_canvas_dragged[0] != null) {
                    place_the_canvas_text(helloController);
                    place_the_box_surrounding_the_text(helloController, text_on_canvas_dragged[0].getLanguage_info());
                    clear_a_canvas(text_on_canvas_dragged[0].getText_position_canvas());
                    helloController.list_view_with_all_of_the_languages.refresh();
                    text_on_canvas_dragged[0] = null;
                }
            }
        });
    }

    private void set_all_the_languages_selected_set_to_false(HelloController helloController) {
        ObservableList<Language_info> all_of_the_languages = helloController.list_view_with_all_of_the_languages.getItems();
        for (Language_info language_info : all_of_the_languages) {
            language_info.setText_box_showing(false);
        }
    }

    private void set_the_holding_cursor_of_image_view_for_text(HelloController helloController, Type_of_cursor type_of_cursor) {
        switch (type_of_cursor) {
            case DEFAULT -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.DEFAULT);
            case CENTER -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.OPEN_HAND);
            case NORTH -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.N_RESIZE);
            case EAST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.E_RESIZE);
            case SOUTH -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.S_RESIZE);
            case WEST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.W_RESIZE);
            case NORTH_EAST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.NE_RESIZE);
            case SOUTH_EAST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.SE_RESIZE);
            case SOUTH_WEST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.SW_RESIZE);
            case NORTH_WEST -> helloController.stack_pane_of_image_view_and_text.setCursor(Cursor.NW_RESIZE);
        }
    }

    private Type_of_cursor return_type_of_cursor_based_on_position(HelloController helloController, MouseEvent mouseEvent, Smallest_text_box_info smallest_text_box_info) {
        if (!smallest_text_box_info.isSet()) {
            return Type_of_cursor.DEFAULT;
        } else {
            Text_box_info smalled_text_box_info = smallest_text_box_info.getText_box_info();
            Language_info language_info = smallest_text_box_info.getLanguage_info();
            Point2D canvas_relative_x_and_y = new Point2D(mouseEvent.getX() / helloController.canvas_displaying_the_verses.getScaleX(), mouseEvent.getY() / helloController.canvas_displaying_the_verses.getScaleY());
            boolean close_to_left = false;
            boolean close_to_top = false;
            boolean close_to_right = false;
            boolean close_to_bottom = false;
            double local_horizontal_distance_for_resize;
            double local_vertical_distance_for_resize;
            byte number_of_edges_the_mouse_is_close_to = 0;
            if (smalled_text_box_info.getText_box_width() >= distance_for_resize_arrow_text_on_cavnas * 3) {
                local_horizontal_distance_for_resize = distance_for_resize_arrow_text_on_cavnas;
            } else {
                local_horizontal_distance_for_resize = smalled_text_box_info.getText_box_width() / 3D;
            }
            if (smalled_text_box_info.getText_box_height() >= distance_for_resize_arrow_text_on_cavnas * 3) {
                local_vertical_distance_for_resize = distance_for_resize_arrow_text_on_cavnas;
            } else {
                local_vertical_distance_for_resize = smalled_text_box_info.getText_box_height() / 3D;
            }
            if (canvas_relative_x_and_y.getX() - smalled_text_box_info.getMin_x_point() <= local_horizontal_distance_for_resize) {
                close_to_left = true;
                number_of_edges_the_mouse_is_close_to++;
            }
            if (smalled_text_box_info.getMax_x_point() - canvas_relative_x_and_y.getX() <= local_horizontal_distance_for_resize) {
                close_to_right = true;
                number_of_edges_the_mouse_is_close_to++;
            }
            if (canvas_relative_x_and_y.getY() - smalled_text_box_info.getMin_y_point() <= local_vertical_distance_for_resize) {
                close_to_top = true;
                number_of_edges_the_mouse_is_close_to++;
            }
            if (smalled_text_box_info.getMax_y_point() - canvas_relative_x_and_y.getY() <= local_vertical_distance_for_resize) {
                close_to_bottom = true;
                number_of_edges_the_mouse_is_close_to++;
            }
            if (number_of_edges_the_mouse_is_close_to == 0) {
                return Type_of_cursor.CENTER;
            } else {
                if (close_to_top) {
                    return Type_of_cursor.NORTH;
                } else if (close_to_bottom) {
                    return Type_of_cursor.SOUTH;
                } else if (close_to_right) {
                    return Type_of_cursor.EAST;
                } else {
                    return Type_of_cursor.WEST;
                }
            }
            /*else if (number_of_edges_the_mouse_is_close_to == 1) {
                if (close_to_top) {
                    return Type_of_cursor.NORTH;
                } else if (close_to_right) {
                    return Type_of_cursor.EAST;
                } else if (close_to_bottom) {
                    return Type_of_cursor.SOUTH;
                } else {
                    return Type_of_cursor.WEST;
                }
            } else {
                if (close_to_top && close_to_left) {
                    return Type_of_cursor.NORTH_WEST;
                } else if (close_to_top && close_to_right) {
                    return Type_of_cursor.NORTH_EAST;
                } else if (close_to_bottom && close_to_right) {
                    return Type_of_cursor.SOUTH_EAST;
                } else {
                    return Type_of_cursor.SOUTH_WEST;
                }
            }*/
        }
    }

    private Smallest_text_box_info return_the_smallest_text_box_info(HelloController helloController, ObservableList<Language_info> all_of_the_languages, MouseEvent mouseEvent) {
        Smallest_text_box_info smalled_text_box_info = new Smallest_text_box_info();
        double min_area = Double.MAX_VALUE;
        for (Language_info language_info : all_of_the_languages) {
            if (language_info.isVisible_check_mark_checked()) {
                Text_box_info text_box_info = language_info.getArrayList_of_all_of_the_translations().get(selected_verse).getText_box_info();
                Point2D canvas_relative_x_and_y = new Point2D(mouseEvent.getX() / helloController.canvas_displaying_the_verses.getScaleX(), mouseEvent.getY() / helloController.canvas_displaying_the_verses.getScaleY());
                if (text_box_info.isVisible() && canvas_relative_x_and_y.getX() >= text_box_info.getMin_x_point() && canvas_relative_x_and_y.getX() <= text_box_info.getMax_x_point() && canvas_relative_x_and_y.getY() >= text_box_info.getMin_y_point() && canvas_relative_x_and_y.getY() <= text_box_info.getMax_y_point() && text_box_info.get_area() < min_area) {
                    smalled_text_box_info = new Smallest_text_box_info(text_box_info, language_info);
                    min_area = text_box_info.get_area();
                }
            }
        }
        return smalled_text_box_info;
    }

    /*private void remove_all_of_the_text_boxes(HelloController helloController, ListView<Language_info> all_of_the_languages) {
        for (Language_info language_info : all_of_the_languages.getItems()) {
            if (language_info.isVisible_check_mark_checked() && language_info.getLanguage_canvas() != null) {
                place_the_canvas_text(helloController, language_info.getLanguage_canvas(), language_info.getArrayList_of_all_of_the_translations().get(selected_verse));
                language_info.setText_box_showing(false);
            }
        }
    }*/

    private void listen_to_render_video_button_clicked(HelloController helloController) {
        helloController.render_video.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                send_analytics_event("render_video_button_clicked");
                show_a_dialogue_asking_user_to_choose_video_render_save_location(helloController);
            }
        });
    }

    private void listen_to_help_app_settings_button(HelloController helloController) {
        helloController.setting_beside_help_spread_the_app.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                send_analytics_event("Help spread sabrly settings used");
                set_all_the_languages_selected_set_to_false(helloController);
            }
        });
    }

    private void bind_the_height_for_settings_icon_to_help_spread_app_button(HelloController helloController) {
        final double add_width_and_height_to_button_size = 5;
        final int minus_from_logo_button = 4;
        helloController.setting_beside_help_spread_the_app.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                helloController.setting_beside_help_spread_the_app.setGraphic(return_the_icon("settings", new_number.intValue() - minus_from_logo_button, new_number.intValue() - minus_from_logo_button));
            }
        });
        helloController.setting_beside_help_spread_the_app.minHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.setting_beside_help_spread_the_app.prefHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.setting_beside_help_spread_the_app.maxHeightProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.setting_beside_help_spread_the_app.minWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.setting_beside_help_spread_the_app.prefWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
        helloController.setting_beside_help_spread_the_app.maxWidthProperty().bind(helloController.check_box_saying_help_spread_the_app.heightProperty().add(add_width_and_height_to_button_size));
    }

    private void set_up_the_icon_for_settings_beside_help_spread_app(HelloController helloController) {
        helloController.setting_beside_help_spread_the_app.setShape(return_default_squircle());
        helloController.setting_beside_help_spread_the_app.setGraphic(return_the_icon("settings", 20, 20));
        helloController.setting_beside_help_spread_the_app.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.setting_beside_help_spread_the_app.setAlignment(Pos.CENTER);
    }

    private void create_and_attach_all_the_image_listeners(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        create_and_attach_the_opacity_listener(helloController, shape_object_time_line);
        create_and_attach_the_fade_in_listener(helloController, shape_object_time_line);
        create_and_attach_the_fade_out_listener(helloController, shape_object_time_line);
    }

    private void create_and_attach_the_opacity_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getOpacity_change_listener();
        if (!listener_info.isListener_set()) {
            ChangeListener<Number> opacity_change_listener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                    helloController.label_holding_the_opacity_percentage.setText(String.valueOf(new_number.intValue()) + "%");
                    opacity_settings.setOpacity(new_number.doubleValue());
                    helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(opacity_settings.return_total_opacity(time_difference_compared_to_start(shape_object_time_line, helloController.time_line_pane), time_difference_compared_to_end(shape_object_time_line, helloController.time_line_pane)));
                    send_analytics_event("Image opacity altered");
                }
            };
            listener_info.setChange_listener(opacity_change_listener);
            listener_info.setListener_set(true);
        }
        if (!listener_info.isListener_attached()) {
            helloController.slider_to_control_the_opacity_of_an_image.valueProperty().addListener(listener_info.getChange_listener());
            listener_info.setListener_attached(true);
            array_list_with_all_of_the_image_control_listeners.add(listener_info);
        }
    }

    private void create_and_attach_the_fade_in_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getFade_in_change_listener();
        if (!listener_info.isListener_set()) {
            ChangeListener<Number> fade_in_change_listener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                    helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(new_number.doubleValue()) + unit_sign_beside_fade_in_fade_out);
                    opacity_settings.setFade_in(new_number.doubleValue());
                    set_the_opacity_of_image_view_considering_everything(helloController, shape_object_time_line);
                    Media_pool media_pool = hashMap_with_media_pool_items.get(shape_object_time_line.getImage_id());
                    set_up_the_image_rectangle((Rectangle) shape_object_time_line.getShape(), remove_transparent_pixels(media_pool.getThumbnail()), helloController.time_line_pane, opacity_settings);
                    send_analytics_event("Image fade in altered");
                }
            };
            listener_info.setChange_listener(fade_in_change_listener);
            listener_info.setListener_set(true);
        }
        if (!listener_info.isListener_attached()) {
            helloController.slider_to_control_fade_in_of_image.valueProperty().addListener(listener_info.getChange_listener());
            listener_info.setListener_attached(true);
            array_list_with_all_of_the_image_control_listeners.add(listener_info);
        }
    }

    private void create_and_attach_the_fade_out_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getFade_out_change_listener();
        if (!listener_info.isListener_set()) {
            ChangeListener<Number> fade_out_change_listener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number old_number, Number new_number) {
                    helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(new_number.doubleValue()) + unit_sign_beside_fade_in_fade_out);
                    opacity_settings.setFade_out(new_number.doubleValue());
                    set_the_opacity_of_image_view_considering_everything(helloController, shape_object_time_line);
                    Media_pool media_pool = hashMap_with_media_pool_items.get(shape_object_time_line.getImage_id());
                    set_up_the_image_rectangle((Rectangle) shape_object_time_line.getShape(), remove_transparent_pixels(media_pool.getThumbnail()), helloController.time_line_pane, opacity_settings);
                    send_analytics_event("Image fade out altered");
                }
            };
            listener_info.setChange_listener(fade_out_change_listener);
            listener_info.setListener_set(true);

        }
        if (!listener_info.isListener_attached()) {
            helloController.slider_to_control_fade_out_of_image.valueProperty().addListener(listener_info.getChange_listener());
            listener_info.setListener_attached(true);
            array_list_with_all_of_the_image_control_listeners.add(listener_info);
        }
    }

    private void set_the_opacity_of_image_view_considering_everything(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(opacity_settings.return_total_opacity(time_difference_compared_to_start(shape_object_time_line, helloController.time_line_pane), time_difference_compared_to_end(shape_object_time_line, helloController.time_line_pane)));

    }

    private double time_difference_compared_to_start(Shape_object_time_line shape_object_time_line, Pane pane) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos_pixels = return_polygon_middle_position(time_line_pane_data);
        double x_pos_time = pixels_to_nanoseconds(time_line_pane_data, x_pos_pixels - time_line_pane_data.getTime_line_base_line());
        double start = shape_object_time_line.getStart_time();
        return x_pos_time - start;
    }

    private double time_difference_compared_to_end(Shape_object_time_line shape_object_time_line, Pane pane) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) pane.getUserData();
        double x_pos_pixels = return_polygon_middle_position(time_line_pane_data);
        double x_pos_time = pixels_to_nanoseconds(time_line_pane_data, x_pos_pixels - time_line_pane_data.getTime_line_base_line());
        double end = shape_object_time_line.getEnd_time();
        return end - x_pos_time;
    }


    private void detach_all_the_image_control_listeners(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        detach_the_opacity_listener(helloController, shape_object_time_line);
        detach_the_fade_in_listener(helloController, shape_object_time_line);
        detach_the_fade_out_listener(helloController, shape_object_time_line);
        array_list_with_all_of_the_image_control_listeners.clear();
    }

    private void detach_the_opacity_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getOpacity_change_listener();
        if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
            helloController.slider_to_control_the_opacity_of_an_image.valueProperty().removeListener(listener_info.getChange_listener());
            listener_info.setListener_attached(false);
        }
    }

    private void detach_the_fade_in_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getFade_in_change_listener();
        if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
            helloController.slider_to_control_fade_in_of_image.valueProperty().removeListener(listener_info.getChange_listener());
            listener_info.setListener_attached(false);
        }
    }

    private void detach_the_fade_out_listener(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        Listener_info listener_info = opacity_settings.getFade_out_change_listener();
        if (listener_info.isListener_set() && listener_info.getChange_listener() != null) {
            helloController.slider_to_control_fade_out_of_image.valueProperty().removeListener(listener_info.getChange_listener());
            listener_info.setListener_attached(false);
        }
    }

    private void enable_the_image_control_panel(HelloController helloController, Shape_object_time_line shape_object_time_line) {
        Opacity_settings opacity_settings = shape_object_time_line.getOpacity_settings();
        helloController.image_controls_stack_pane.setDisable(false);
        helloController.slider_to_control_the_opacity_of_an_image.setValue(opacity_settings.getOpacity());
        helloController.label_holding_the_opacity_percentage.setText(String.valueOf((int) helloController.slider_to_control_the_opacity_of_an_image.getValue()) + unit_sign_beside_opacity);
        helloController.slider_to_control_fade_in_of_image.setValue(opacity_settings.getFade_in());
        helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(opacity_settings.getFade_in()) + unit_sign_beside_fade_in_fade_out);
        helloController.slider_to_control_fade_out_of_image.setValue(opacity_settings.getFade_out());
        helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(opacity_settings.getFade_out()) + unit_sign_beside_fade_in_fade_out);
    }

    private void disable_the_image_control_panel(HelloController helloController) {
        helloController.image_controls_stack_pane.setDisable(true);
        helloController.slider_to_control_the_opacity_of_an_image.setValue(100);
        helloController.label_holding_the_opacity_percentage.setText("100" + unit_sign_beside_opacity);
        helloController.slider_to_control_fade_in_of_image.setValue(0);
        helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_in_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);
        helloController.slider_to_control_fade_out_of_image.setValue(0);
        helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_out_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);

            /*helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(0);
            helloController.slider_to_control_the_opacity_of_an_image.setValue(100);
            helloController.slider_to_control_fade_in_of_image.setValue(0);
            helloController.slider_to_control_fade_out_of_image.setValue(0);
            helloController.label_holding_the_opacity_percentage.setText("100" + unit_sign_beside_opacity);
            helloController.label_holding_the_fade_in.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_in_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);
            helloController.label_holding_the_fade_out.setText(return_formatted_string_to_1_decimal_place_always(helloController.slider_to_control_fade_out_of_image.getMin()) + unit_sign_beside_fade_in_fade_out);*/
    }

    private void set_up_the_fade_in_fade_out_slider_ticks(HelloController helloController) {
        set_up_the_tick_marks_for_a_slider(helloController.slider_to_control_fade_in_of_image, 0.1);
        set_up_the_tick_marks_for_a_slider(helloController.slider_to_control_fade_out_of_image, 0.1);
        set_up_the_tick_marks_for_a_slider(helloController.slider_to_control_the_opacity_of_an_image, 1);
    }

    private void set_up_the_tick_marks_for_a_slider(Slider slider, double major_tick_units) {
        slider.setShowTickMarks(false);
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(major_tick_units);
        slider.setMinorTickCount(0);
    }

    private void reset_the_opacity(HelloController helloController) {
        helloController.rectangle_on_top_of_chat_gpt_image_view_for_opacity_tint.setOpacity(0);
    }

    private void set_up_everything_image_view_time_line_time(HelloController helloController, long time_in_nanos, Type_of_Image type_of_image) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        double x_pos = nanoseconds_to_pixels(time_line_pane_data, time_in_nanos) + time_line_pane_data.getTime_line_base_line();
        Shape_object_time_line shape_object_time_line_returned_on_click = return_the_shape_on_click(helloController.time_line_pane, x_pos);
        set_up_everything_image_view_time_line_shape_object_time_line(helloController, shape_object_time_line_returned_on_click, type_of_image);
    }

    private void set_up_everything_image_view_time_line_x_pos(HelloController helloController, double x_pos, Type_of_Image type_of_image) {
        Shape_object_time_line shape_object_time_line_returned_on_click = return_the_shape_on_click(helloController.time_line_pane, x_pos);
        set_up_everything_image_view_time_line_shape_object_time_line(helloController, shape_object_time_line_returned_on_click, type_of_image);
    }

    private void set_up_everything_image_view_time_line_shape_object_time_line(HelloController helloController, Shape_object_time_line shape_object_time_line, Type_of_Image type_of_image) {
        if (shape_object_time_line != null) {
            set_the_chatgpt_image_view(helloController, shape_object_time_line.getImage_id(), type_of_image);
            set_up_the_image_controller_enable_disable(helloController, shape_object_time_line);
            set_the_opacity_of_image_view_considering_everything(helloController, shape_object_time_line);
        } else {
            set_the_chatgpt_image_view(helloController, no_image_found, Type_of_Image.FULL_QUALITY);
            detach_the_listeners_image_control_unknown(helloController);
            disable_the_image_control_panel(helloController);
            reset_the_opacity(helloController);
        }
    }

    private void set_up_icon_for_settings_mark_button_on_loading_screen(HelloController helloController) {
        helloController.settings_loading_screen.setShape(return_default_squircle());
        helloController.settings_loading_screen.setGraphic(return_the_icon("gold_settings_icon", 20, 20));
        helloController.settings_loading_screen.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.settings_loading_screen.setAlignment(Pos.CENTER);
    }

    private void set_the_cursor_for_question_mark_button_on_loading_screen(HelloController helloController) {
        helloController.settings_loading_screen.setCursor(Cursor.HAND);
    }

    private void hide_the_settings_mark_button_loading_screen_when_done(HelloController helloController) {
        helloController.settings_loading_screen.setVisible(false);
        helloController.settings_loading_screen.setManaged(false);
    }

    private void listen_to_settings_button_clicked_loading_screen(HelloController helloController) {
        helloController.settings_loading_screen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                show_feedback_settings_dialog("Settings");
                send_analytics_event("settings_clicked");
            }
        });
    }

    private void show_feedback_settings_dialog(String dialog_title) {
        if (app_settings_information_stage != null && app_settings_information_stage.isShowing()) {
            app_settings_information_stage.toFront();
            return;
        }
        final int vertical_margin = 40;
        final int horizontal_margin = 30;
        app_settings_information_stage = new Stage();
        app_settings_information_stage.initOwner(main_stage);
        app_settings_information_stage.initStyle(StageStyle.DECORATED);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label app_version_label = new Label("Version: " + app_version);
        Label label_saying_to_user_main_stuff = new Label("If you’d like to share feedback or report a bug, feel free to send me an email\n or join our Discord community using the link below.");
        Label label_saying_help_email = new Label(help_email);
        HBox hbox_hosting_copy_buttons = new HBox();
        JFXButton copy_email_button = new JFXButton("Copy email");
        JFXButton copy_discord_invite_link_button = new JFXButton("Copy Discord invite link");
        JFXButton got_it_button = new JFXButton("Got it");
        Separator empty_separator_at_the_bottom = new Separator();
        copy_email_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                copyToClipboard(help_email);
                showToast(app_settings_information_stage, "Copied", 2500);
            }
        });
        copy_discord_invite_link_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                copyToClipboard(discord_invite_link);
                showToast(app_settings_information_stage, "Copied", 2500);
            }
        });
        got_it_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                app_settings_information_stage.close();
            }
        });

        VBox.setMargin(app_version_label, new Insets(vertical_margin, horizontal_margin, 0, horizontal_margin));

        VBox.setMargin(label_saying_to_user_main_stuff, new Insets(25, horizontal_margin, 0, horizontal_margin));

        VBox.setMargin(label_saying_help_email, new Insets(25, horizontal_margin, 0, horizontal_margin));

        VBox.setMargin(hbox_hosting_copy_buttons, new Insets(25, horizontal_margin, 0, horizontal_margin));

        VBox.setMargin(got_it_button, new Insets(25, horizontal_margin, 0, horizontal_margin));

        VBox.setMargin(empty_separator_at_the_bottom, new Insets(vertical_margin, 0, 0, 0));

        HBox.setMargin(copy_discord_invite_link_button, new Insets(0, 0, 0, 15));
        hbox_hosting_copy_buttons.setAlignment(Pos.CENTER);

        app_version_label.setAlignment(Pos.CENTER);
        app_version_label.setTextAlignment(TextAlignment.CENTER);
        app_version_label.setWrapText(true);

        label_saying_to_user_main_stuff.setAlignment(Pos.CENTER);
        label_saying_to_user_main_stuff.setTextAlignment(TextAlignment.CENTER); // center each line of text
        label_saying_to_user_main_stuff.setWrapText(true);

        copy_email_button.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
        copy_email_button.setFocusTraversable(false);

        copy_discord_invite_link_button.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
        copy_discord_invite_link_button.setFocusTraversable(false);

        got_it_button.setStyle("-fx-background-color: #000000; -fx-text-fill: #FFFFFF;");
        got_it_button.setFocusTraversable(false);

        empty_separator_at_the_bottom.setVisible(false);

        hbox_hosting_copy_buttons.getChildren().addAll(copy_email_button, copy_discord_invite_link_button);

        vBox.getChildren().addAll(app_version_label, label_saying_to_user_main_stuff, label_saying_help_email, hbox_hosting_copy_buttons, got_it_button, empty_separator_at_the_bottom);
        Scene scene = new Scene(vBox);
        app_settings_information_stage.setScene(scene);
        app_settings_information_stage.sizeToScene();
        app_settings_information_stage.setTitle(dialog_title);
        app_settings_information_stage.show();
    }

    private void loop_through_all_verses_and_update(HelloController helloController, ListView<Language_info> all_translations) {
        ArrayList<Language_info> array_list_of_language_info_with_the_text_box_showing = new ArrayList<>();
        for (Language_info language_info : all_translations.getItems()) {
            if (language_info.isVisible_check_mark_checked()) {
                place_the_canvas_text(helloController);
                if (language_info.isText_box_showing()) {
                    array_list_of_language_info_with_the_text_box_showing.add(language_info);
                }
            }
        }
        if (!array_list_of_language_info_with_the_text_box_showing.isEmpty()) {
            place_the_box_surrounding_the_text(helloController, array_list_of_language_info_with_the_text_box_showing.getLast());
        }
    }

    private void set_up_the_text_position_canvas(HelloController helloController) {
        helloController.canvas_holding_the_text_position.setHeight(1920);
        helloController.canvas_holding_the_text_position.setWidth(1080);
        bind_the_canvas_to_the_image_view(helloController, helloController.canvas_holding_the_text_position);
    }

    private void clear_a_canvas(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void create_the_split_button(HelloController helloController) {
        final double width_and_height_of_the_split_button = 15;
        Rectangle rounded_rectangle = new Rectangle(-1, -1, 2, 2);
        rounded_rectangle.setArcWidth(0.7);
        rounded_rectangle.setArcHeight(0.7);
        set_pref_min_max(helloController.split_verse_button, width_and_height_of_the_split_button * 2, Resize_bind_type.WIDTH_AND_HEIGHT);
        helloController.split_verse_button.setShape(rounded_rectangle);
        helloController.split_verse_button.setBackground(new Background(new BackgroundFill(new javafx.scene.paint.Color(0, 0, 0, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        helloController.split_verse_button.setRipplerFill(new javafx.scene.paint.Color(1, 0, 0, 1));
        helloController.split_verse_button.setGraphic(return_the_icon("split_icon", (int) (width_and_height_of_the_split_button * 1.25D), (int) (width_and_height_of_the_split_button * 1.25D)));
        helloController.split_verse_button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        helloController.split_verse_button.setAlignment(Pos.CENTER);
        helloController.split_verse_button.setLayoutX(7.5D);
        helloController.split_verse_button.setLayoutY(7.5D);
    }

    private void add_split_verse_button_tooltip(HelloController helloController) {
        Tooltip tooltip = new Tooltip("Split verse");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.split_verse_button.setTooltip(tooltip);
    }

    /*private void add_fast_reverse_tool_tip(HelloController helloController){
        Tooltip tooltip = new Tooltip("Rewind 1 min");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.fast_rewind_button.setTooltip(tooltip);
    }

    private void add_reverse_tool_tip(HelloController helloController){
        Tooltip tooltip = new Tooltip("Previous verse");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.rewind_button.setTooltip(tooltip);
    }

    private void add_play_pause_tool_tip(HelloController helloController){
        Tooltip tooltip = new Tooltip("Play/Pause");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.play_pause_button.setTooltip(tooltip);
    }

    private void add_forward_tool_tip(HelloController helloController){
        Tooltip tooltip = new Tooltip("Next verse");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.forward_button.setTooltip(tooltip);
    }

    private void add_fast_forward_tool_tip(HelloController helloController){
        Tooltip tooltip = new Tooltip("Forward 1 min");
        tooltip.setShowDelay(Duration.millis(how_long_does_it_take_for_tool_tip_to_show_up));
        helloController.fast_forward_button.setTooltip(tooltip);
    }*/

    private void set_the_buttons_color_change_when_hovered(HelloController helloController) {
        change_color_of_button_when_hovered(helloController.split_verse_button, new javafx.scene.paint.Color(0.25, 0.25, 0.25, 1), new javafx.scene.paint.Color(0, 0, 0, 1));

        javafx.scene.paint.Color old_default_color = new javafx.scene.paint.Color(1, 1, 1, 1);
        javafx.scene.paint.Color new_hovered_color = new javafx.scene.paint.Color(0.9, 0.9, 0.9, 1);

        change_color_of_button_when_hovered(helloController.fast_rewind_button, new_hovered_color, old_default_color);
        change_color_of_button_when_hovered(helloController.rewind_button, new_hovered_color, old_default_color);
        change_color_of_button_when_hovered(helloController.play_pause_button, new_hovered_color, old_default_color);
        change_color_of_button_when_hovered(helloController.forward_button, new_hovered_color, old_default_color);
        change_color_of_button_when_hovered(helloController.fast_forward_button, new_hovered_color, old_default_color);

        change_color_of_button_when_hovered(helloController.setting_beside_help_spread_the_app, new_hovered_color, old_default_color);
        change_color_of_button_when_hovered(helloController.question_mark_beside_help_spread_the_app, new_hovered_color, old_default_color);

    }

    private void change_color_of_button_when_hovered(Button button, javafx.scene.paint.Color new_button_color, javafx.scene.paint.Color old_button_color) {
        button.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                button.setBackground(new Background(new BackgroundFill(new_button_color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        button.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                button.setBackground(new Background(new BackgroundFill(old_button_color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
    }

    private void listen_to_split_verse(HelloController helloController) {
        JFXSnackbar cant_split_snack_bar = new JFXSnackbar(helloController.pane_overlying_the_time_line_pane_for_polygon_indicator);
        StackPane stack_pane_with_bottom_inset = new StackPane();

        StackPane snack_bar_stack_pane = new StackPane();

        Label verse_segment_cant_be_short_stack_pane = new Label("Verse segment can't be shorter than 250 milliseconds.");
        StackPane.setMargin(verse_segment_cant_be_short_stack_pane, new Insets(7.5, 10, 7.5, 10));

        Rectangle snack_bar_background = new Rectangle();
        snack_bar_background.setArcWidth(7.5);
        snack_bar_background.setArcHeight(7.5);
        snack_bar_background.setFill(new javafx.scene.paint.Color(1, 1, 1, 1));

        snack_bar_background.widthProperty().bind(snack_bar_stack_pane.widthProperty());
        snack_bar_background.heightProperty().bind(snack_bar_stack_pane.heightProperty());

        snack_bar_stack_pane.getChildren().addAll(snack_bar_background, verse_segment_cant_be_short_stack_pane);

        StackPane.setMargin(snack_bar_stack_pane, new Insets(0, 0, 10, 0));

        stack_pane_with_bottom_inset.getChildren().add(snack_bar_stack_pane);

        Duration snack_bar_duration = new Duration(3000);
        helloController.split_verse_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                send_analytics_event("split_verse_clicked");
                Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
                long polygon_time_in_milliseconds = TimeUnit.NANOSECONDS.toMillis(pixels_to_nanoseconds(time_line_pane_data, return_polygon_middle_position(time_line_pane_data) - time_line_pane_data.getTime_line_base_line()));
                long verse_start_in_milliseconds = TimeUnit.NANOSECONDS.toMillis(ayats_processed.get(selected_verse).getStart_millisecond());
                long verse_duration_in_milliseconds = TimeUnit.NANOSECONDS.toMillis(ayats_processed.get(selected_verse).getDuration());
                if (polygon_time_in_milliseconds - verse_start_in_milliseconds < 250 || verse_start_in_milliseconds + verse_duration_in_milliseconds - polygon_time_in_milliseconds < 250) {
                    cant_split_snack_bar.enqueue(new JFXSnackbar.SnackbarEvent(stack_pane_with_bottom_inset, snack_bar_duration));
                } else {
                    split_the_verse_time_line(helloController);
                }
            }
        });
    }

    private void listen_to_mouse_moved_inside_rectangle(HelloController helloController, Time_line_pane_data time_line_pane_data, int verse_array_number, StackPane main_stack_pane, Rectangle main_rectangle, StackPane previous_stack_pane, Rectangle previous_rectangle, StackPane next_stack_pane, Rectangle next_rectangle, Verse_position_mode verse_position_mode) {
        final double rectangle_cursor_change_margin = 12.5D;
        final long auto_scroll_waiting_threshold = 50;
        final double auto_scroll_move_by = 20;
        final Double[] last_seen_mouse_x_position = {0D};
        final Boolean[] last_seen_x_position_set = {false};
        final double time_lane_start_margin = StackPane.getMargin(helloController.scroll_pane_hosting_the_time_line).getLeft();
        final Verse_resize_info[] verse_resize_info = new Verse_resize_info[1];

        AnimationTimer verse_held_animation_timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (verse_resize_info[0] != null && last_seen_x_position_set[0]) {
                    if (last_seen_mouse_x_position[0] <= time_lane_start_margin && System.currentTimeMillis() - verse_resize_info[0].getLast_out_of_scene_update() >= auto_scroll_waiting_threshold) {
                        verse_resize_info[0].setLast_out_of_scene_update(System.currentTimeMillis());
                        double contentWidth = helloController.scroll_pane_hosting_the_time_line.getContent().getBoundsInLocal().getWidth();
                        double viewportWidth = helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
                        double old_x_value = helloController.scroll_pane_hosting_the_time_line.getHvalue() * (contentWidth - viewportWidth);
                        double new_x_value = old_x_value - auto_scroll_move_by;
                        double new_h_value = new_x_value / (contentWidth - viewportWidth);
                        new_h_value = Math.max(0, new_h_value);
                        helloController.scroll_pane_hosting_the_time_line.setHvalue(new_h_value);
                        update_the_verse_stack_pane_based_on_mouse(helloController, verse_resize_info[0], time_line_pane_data, new_x_value, 0, verse_array_number, main_stack_pane, main_rectangle, next_stack_pane, next_rectangle, previous_stack_pane, previous_rectangle, last_seen_mouse_x_position, last_seen_x_position_set);
                    }
                    if (last_seen_mouse_x_position[0] >= time_lane_start_margin + helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth() && System.currentTimeMillis() - verse_resize_info[0].getLast_out_of_scene_update() >= auto_scroll_waiting_threshold) {
                        verse_resize_info[0].setLast_out_of_scene_update(System.currentTimeMillis());
                        double contentWidth = helloController.scroll_pane_hosting_the_time_line.getContent().getBoundsInLocal().getWidth();
                        double viewportWidth = helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth();
                        double old_x_value = helloController.scroll_pane_hosting_the_time_line.getHvalue() * (contentWidth - viewportWidth);
                        double new_x_value = old_x_value + auto_scroll_move_by;
                        double new_h_value = new_x_value / (contentWidth - viewportWidth);
                        new_h_value = Math.min(1, new_h_value);
                        helloController.scroll_pane_hosting_the_time_line.setHvalue(new_h_value);
                        update_the_verse_stack_pane_based_on_mouse(helloController, verse_resize_info[0], time_line_pane_data, new_x_value + viewportWidth, time_lane_start_margin + helloController.scroll_pane_hosting_the_time_line.getViewportBounds().getWidth(), verse_array_number, main_stack_pane, main_rectangle, next_stack_pane, next_rectangle, previous_stack_pane, previous_rectangle, last_seen_mouse_x_position, last_seen_x_position_set);
                    }
                }
            }
        };

        main_stack_pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getX() <= rectangle_cursor_change_margin && (verse_position_mode == Verse_position_mode.MIDDLE || verse_position_mode == Verse_position_mode.END)) {
                    main_stack_pane.setCursor(Cursor.W_RESIZE);
                } else if (main_stack_pane.getWidth() - mouseEvent.getX() <= rectangle_cursor_change_margin && (verse_position_mode == Verse_position_mode.MIDDLE || verse_position_mode == Verse_position_mode.START)) {
                    main_stack_pane.setCursor(Cursor.E_RESIZE);
                } else {
                    main_stack_pane.setCursor(Cursor.DEFAULT);
                }
            }
        });
        main_stack_pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                send_analytics_event("verse_rectnagle_clicked");
                Point2D time_line_mouse_position = main_stack_pane.localToParent(mouseEvent.getX(), mouseEvent.getY());
                double polygon_x_position = return_polygon_middle_position(time_line_pane_data);
                if (mouseEvent.getX() <= rectangle_cursor_change_margin && (verse_position_mode == Verse_position_mode.MIDDLE || verse_position_mode == Verse_position_mode.END)) {
                    Polygon_position polygon_position;
                    if (polygon_x_position <= main_stack_pane.getLayoutX()) {
                        polygon_position = Polygon_position.BEFORE;
                    } else {
                        polygon_position = Polygon_position.AFTER;
                    }
                    verse_resize_info[0] = new Verse_resize_info(Resizing_mode.WEST, time_line_mouse_position.getX(), true, main_stack_pane, previous_stack_pane, next_stack_pane, polygon_position, polygon_x_position);
                } else if (main_stack_pane.getWidth() - mouseEvent.getX() <= rectangle_cursor_change_margin && (verse_position_mode == Verse_position_mode.MIDDLE || verse_position_mode == Verse_position_mode.START)) {
                    Polygon_position polygon_position;
                    if (polygon_x_position <= main_stack_pane.getLayoutX() + main_stack_pane.getWidth()) {
                        polygon_position = Polygon_position.BEFORE;
                    } else {
                        polygon_position = Polygon_position.AFTER;
                    }
                    verse_resize_info[0] = new Verse_resize_info(Resizing_mode.EAST, time_line_mouse_position.getX(), true, main_stack_pane, previous_stack_pane, next_stack_pane, polygon_position, polygon_x_position);
                } else {
                    verse_resize_info[0] = new Verse_resize_info(false);
                }
                verse_held_animation_timer.start();
            }
        });
        main_stack_pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Point2D time_line_mouse_position = main_stack_pane.localToParent(mouseEvent.getX(), mouseEvent.getY());
                update_the_verse_stack_pane_based_on_mouse(helloController, verse_resize_info[0], time_line_pane_data, time_line_mouse_position.getX(), mouseEvent.getSceneX(), verse_array_number, main_stack_pane, main_rectangle, next_stack_pane, next_rectangle, previous_stack_pane, previous_rectangle, last_seen_mouse_x_position, last_seen_x_position_set);
            }
        });
        main_stack_pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                verse_resize_info[0] = null;
                verse_held_animation_timer.stop();
                last_seen_x_position_set[0] = false;
            }
        });
    }

    private void update_the_verse_stack_pane_based_on_mouse(HelloController helloController, Verse_resize_info verse_resize_info, Time_line_pane_data time_line_pane_data, double time_line_mouse_position, double mouse_event_last_seen_scene_x_position, int verse_array_number, StackPane main_stack_pane, Rectangle main_rectangle, StackPane next_stack_pane, Rectangle next_rectangle, StackPane previous_stack_pane, Rectangle previous_rectangle, Double[] last_seen_mouse_x_position, Boolean[] last_seen_x_position_set) {
        if (verse_resize_info != null && verse_resize_info.isSet()) {
            if (verse_resize_info.getResizing_mode() == Resizing_mode.EAST) {
                double new_width = verse_resize_info.getVerse_width() + time_line_mouse_position - verse_resize_info.getInitial_mouse_x_position();
                new_width = Math.max(new_width, nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250)));
                if (verse_resize_info.getNext_verse_width() + verse_resize_info.getVerse_width() - new_width < nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250))) {
                    new_width = verse_resize_info.getNext_verse_width() + verse_resize_info.getVerse_width() - nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250));
                }
                main_stack_pane.setPrefWidth(new_width);
                main_stack_pane.setMinWidth(new_width);
                main_stack_pane.setMaxWidth(new_width);
                main_rectangle.setWidth(new_width);

                double width_difference = verse_resize_info.getVerse_width() - new_width;
                double next_verse_new_width = verse_resize_info.getNext_verse_width() + width_difference;
                double next_verse_start = verse_resize_info.getNext_verse_start_x() - width_difference;
                next_stack_pane.setPrefWidth(next_verse_new_width);
                next_stack_pane.setMinWidth(next_verse_new_width);
                next_stack_pane.setMaxWidth(next_verse_new_width);
                next_rectangle.setWidth(next_verse_new_width);

                next_stack_pane.setLayoutX(next_verse_start);

                ayats_processed.get(verse_array_number).setDuration(pixels_to_nanoseconds(time_line_pane_data, new_width));

                ayats_processed.get(verse_array_number + 1).setStart_millisecond(pixels_to_nanoseconds(time_line_pane_data, next_verse_start - time_line_pane_data.getTime_line_base_line()));
                ayats_processed.get(verse_array_number + 1).setDuration(pixels_to_nanoseconds(time_line_pane_data, next_verse_new_width));
                if (main_stack_pane.getLayoutX() + main_stack_pane.getWidth() < verse_resize_info.getInitial_polygon_x_position() && verse_resize_info.getPolygon_position() == Polygon_position.AFTER) {
                    verse_resize_info.setPolygon_position(Polygon_position.BEFORE);
                    update_the_verse_based_on_milliseconds(helloController, pixels_to_nanoseconds(time_line_pane_data, verse_resize_info.getInitial_polygon_x_position() - time_line_pane_data.getTime_line_base_line()));
                    helloController.list_view_with_all_of_the_languages.refresh();
                } else if (main_stack_pane.getLayoutX() + main_stack_pane.getWidth() >= verse_resize_info.getInitial_polygon_x_position() && verse_resize_info.getPolygon_position() == Polygon_position.BEFORE) {
                    verse_resize_info.setPolygon_position(Polygon_position.AFTER);
                    update_the_verse_based_on_milliseconds(helloController, pixels_to_nanoseconds(time_line_pane_data, verse_resize_info.getInitial_polygon_x_position() - time_line_pane_data.getTime_line_base_line()));
                    helloController.list_view_with_all_of_the_languages.refresh();
                }
                last_seen_mouse_x_position[0] = mouse_event_last_seen_scene_x_position;
                last_seen_x_position_set[0] = true;
            } else if (verse_resize_info.getResizing_mode() == Resizing_mode.WEST) {
                double new_width = verse_resize_info.getVerse_width() - time_line_mouse_position + verse_resize_info.getInitial_mouse_x_position();
                new_width = Math.max(new_width, nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250)));
                if (verse_resize_info.getPrevious_verse_width() - new_width + verse_resize_info.getVerse_width() < nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250))) {
                    new_width = verse_resize_info.getPrevious_verse_width() - nanoseconds_to_pixels(time_line_pane_data, TimeUnit.MILLISECONDS.toNanos(250)) + verse_resize_info.getVerse_width();
                }
                main_stack_pane.setPrefWidth(new_width);
                main_stack_pane.setMinWidth(new_width);
                main_stack_pane.setMaxWidth(new_width);
                main_rectangle.setWidth(new_width);

                double width_difference = new_width - verse_resize_info.getVerse_width();
                double new_previous_verse_width = verse_resize_info.getPrevious_verse_width() - width_difference;
                previous_stack_pane.setPrefWidth(new_previous_verse_width);
                previous_stack_pane.setMinWidth(new_previous_verse_width);
                previous_stack_pane.setMaxWidth(new_previous_verse_width);
                previous_rectangle.setWidth(new_previous_verse_width);

                double new_layout_x = verse_resize_info.getVerse_end_x() - new_width;
                main_stack_pane.setLayoutX(new_layout_x);

                ayats_processed.get(verse_array_number).setStart_millisecond(pixels_to_nanoseconds(time_line_pane_data, new_layout_x - time_line_pane_data.getTime_line_base_line()));
                ayats_processed.get(verse_array_number).setDuration(pixels_to_nanoseconds(time_line_pane_data, new_width));

                ayats_processed.get(verse_array_number - 1).setDuration(pixels_to_nanoseconds(time_line_pane_data, new_previous_verse_width));

                if (main_stack_pane.getLayoutX() < verse_resize_info.getInitial_polygon_x_position() && verse_resize_info.getPolygon_position() == Polygon_position.BEFORE) {
                    verse_resize_info.setPolygon_position(Polygon_position.AFTER);
                    update_the_verse_based_on_milliseconds(helloController, pixels_to_nanoseconds(time_line_pane_data, verse_resize_info.getInitial_polygon_x_position() - time_line_pane_data.getTime_line_base_line()));
                    helloController.list_view_with_all_of_the_languages.refresh();
                } else if (main_stack_pane.getLayoutX() >= verse_resize_info.getInitial_polygon_x_position() && verse_resize_info.getPolygon_position() == Polygon_position.AFTER) {
                    verse_resize_info.setPolygon_position(Polygon_position.BEFORE);
                    update_the_verse_based_on_milliseconds(helloController, pixels_to_nanoseconds(time_line_pane_data, verse_resize_info.getInitial_polygon_x_position() - time_line_pane_data.getTime_line_base_line()));
                    helloController.list_view_with_all_of_the_languages.refresh();
                }
                last_seen_mouse_x_position[0] = mouse_event_last_seen_scene_x_position;
                last_seen_x_position_set[0] = true;
            }
        }
    }

    private void set_up_the_verses_canvas(HelloController helloController) {
        helloController.canvas_displaying_the_verses.setHeight(Global_default_values.translation_canvas_height);
        helloController.canvas_displaying_the_verses.setWidth(Global_default_values.translation_canvas_width);
        bind_the_canvas_to_the_image_view(helloController, helloController.canvas_displaying_the_verses);
    }

    private void close_everything_on_close(Stage stage) {
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private Text_item get_the_made_with_sabrly_text_item(double text_x_position, double text_y_position) {
        Text_item made_with_sabrly_text_item = new Text_item("Made with sabrly.com");
        made_with_sabrly_text_item.setColor(javafx.scene.paint.Color.WHITE);
        Text_accessory_info stroke_info = new Text_accessory_info(Accessory_type.STROKE, 6, Global_default_values.max_stroke_weight);
        Text_accessory_info shadow_info = new Text_accessory_info(Accessory_type.SHADOW, 10, Global_default_values.max_shadow_weight);
        stroke_info.setIs_the_accessory_on(true);
        shadow_info.setIs_the_accessory_on(true);
        made_with_sabrly_text_item.setStroke_info(stroke_info);
        made_with_sabrly_text_item.setShadow_info(shadow_info);
        made_with_sabrly_text_item.getText_box_info().setCenter_position(new Point2D(text_x_position, text_y_position));
        made_with_sabrly_text_item.setFont_size(60);
        return made_with_sabrly_text_item;
    }

    private String create_and_save_client_id_if_it_doesnt_exist() {
        try {
            Preferences prefs = Preferences.userRoot().node("sabrly");
            String id = prefs.get("sabrly_client_id_key", null);

            if (id == null) {
                id = UUID.randomUUID().toString();
                prefs.put("sabrly_client_id_key", id);
                prefs.flush();
            }
            return id;
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }


    private void send_analytics_event(String event_name) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        if (event_name.length() >= 40) {
            throw new RuntimeException("Event name can't be greater than or equal to 40 characters");
        }
        send_analytics_event(event_name, new HashMap<>());
    }

    private void send_analytics_event(String event_name, HashMap<String, Object> items) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        if (!has_enough_time_passed_to_send_an_analytics_event(event_name)) {
            return;
        }
        if (event_name.length() >= 40) {
            throw new RuntimeException("Event name can't be greater than or equal to 40 characters");
        }
        for (String key : items.keySet()) {
            if (key.length() >= 40) {
                throw new RuntimeException("Parameter name can't be greater than or equal to 40 characters");
            }
        }
        try {
            HttpClient HTTP = HttpClient.newHttpClient();
            String json;
            if (items.isEmpty()) {
                json = """
                        {
                          "client_id": "%s",
                          "events": [{
                            "name": "%s"
                          }]
                        }
                        """.formatted(create_and_save_client_id_if_it_doesnt_exist(), event_name);
            } else {
                ObjectMapper mapper = new ObjectMapper();

                ObjectNode root = mapper.createObjectNode();
                root.put("client_id", create_and_save_client_id_if_it_doesnt_exist());

                // Events array
                ArrayNode events = mapper.createArrayNode();
                ObjectNode event = mapper.createObjectNode();
                event.put("name", event_name);

                // Params
                ObjectNode params = mapper.createObjectNode();
                for (String key : items.keySet()) {
                    Object value = items.get(key);
                    if (value instanceof String) {
                        params.put(key, (String) value);
                    } else if (value instanceof Integer) {
                        params.put(key, (Integer) value);
                    } else if (value instanceof Long) {
                        params.put(key, (Long) value);
                    } else if (value instanceof Float) {
                        params.put(key, ((Float) value).doubleValue());
                    } else if (value instanceof Double) {
                        params.put(key, (Double) value);
                    } else if (value instanceof Boolean) {
                        params.put(key, (Boolean) value);
                    }
                }

                event.set("params", params);
                events.add(event);

                root.set("events", events);
                json = mapper.writeValueAsString(root);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://www.google-analytics.com/mp/collect" +
                                    "?measurement_id=" + Quran_api_secrets.analytics_measurement_id +
                                    "&api_secret=" + Quran_api_secrets.analytics_api_secret))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HTTP.sendAsync(request, HttpResponse.BodyHandlers.discarding());

        } catch (Exception exception) {
            // analytics must never break the app
            System.err.println(exception.toString());
        }
    }

    private void send_surat_analytics_event(int chapter_number) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        if (chapter_number <= 0) {
            throw new RuntimeException("surat number can't be less than or equal to 0");
        }
        if (chapter_number > 114) {
            throw new RuntimeException("surat number can't be greater than 114");
        }
        HashMap<String, Object> chapter_hash_map = new HashMap<>();
        chapter_hash_map.put("chapter", chapter_number);
        send_analytics_event("surat_chosen", chapter_hash_map);
    }

    private void send_last_screen_analytics_event(int chapter_number, int start_verse, int end_verse, Sound_mode sound_mode, String reciter_name, ArrayList<String> arabic_verses) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        if (chapter_number <= 0) {
            throw new RuntimeException("surat number can't be less than or equal to 0");
        }
        if (chapter_number > 114) {
            throw new RuntimeException("surat number can't be greater than 114");
        }
        if (start_verse <= 0) {
            throw new RuntimeException("start verse can't be less than or equal to 0");
        }
        if (end_verse <= 0) {
            throw new RuntimeException("end verse can't be less than or equal to 0");
        }
        int verses_generated = end_verse - start_verse + 1;
        String sound_mode_string;
        if (sound_mode == Sound_mode.CHOSEN) {
            sound_mode_string = "chosen";
        } else {
            sound_mode_string = "uploaded";
        }
        StringBuilder arabic_verses_string_builder = new StringBuilder();
        for (String verse : arabic_verses) {
            arabic_verses_string_builder.append(verse).append(' ');
        }
        HashMap<String, Object> event_hashmap = new HashMap<>();
        event_hashmap.put("chapter", chapter_number);
        event_hashmap.put("verses_generated", verses_generated);
        event_hashmap.put("sound_mode", sound_mode_string);
        event_hashmap.put("reciter", reciter_name);
        event_hashmap.put("arabic_letters", countArabicLetters(arabic_verses_string_builder.toString()));
        send_analytics_event("last_screen_opened", event_hashmap);
    }

    private void send_language_expanded_analytics_event(String language_name) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        HashMap<String, Object> event_hashmap = new HashMap<>();
        event_hashmap.put("language", language_name);
        send_analytics_event("language_expanded", event_hashmap);
    }

    private void send_language_selected_analytics_event(String language_name) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }
        HashMap<String, Object> event_hashmap = new HashMap<>();
        event_hashmap.put("language", language_name);
        send_analytics_event("language_expanded", event_hashmap);
    }

    private void send_render_analytics_event(HelloController helloController) {
        if (running_mode == Running_mode.DEBUG) {
            return;
        }

        HashMap<String, Object> event_hashmap = new HashMap<>();
        event_hashmap.put("sound_mode", sound_mode == Sound_mode.CHOSEN ? "chosen" : "uploaded");
        event_hashmap.put("verse_count", ayats_processed.size());
        int unique_verses = (int) ayats_processed.stream()
                .map(Verse_class_final::getVerse_number)
                .distinct()
                .count();
        event_hashmap.put("unique_verse_count", unique_verses);
        long total_duration_ms = TimeUnit.NANOSECONDS.toMillis(get_duration());
        event_hashmap.put("duration_seconds", (int) (total_duration_ms / 1000));
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        int image_rectangle_count = time_line_pane_data.getTree_set_containing_all_of_the_items().size();
        event_hashmap.put("image_count", image_rectangle_count);
        event_hashmap.put("help_spread_sabrly_enabled", helloController.check_box_saying_help_spread_the_app.isSelected());
        event_hashmap.put("surat_name", surat_name_selected);
        ObservableList<Language_info> all_the_languages = helloController.list_view_with_all_of_the_languages.getItems();
        Language_info arabic_language = null;
        for(Language_info language_info : all_the_languages) {
            if(language_info.getLanguage_name().equals("arabic")){
                arabic_language = language_info;
            }
            if(language_info.isVisible_check_mark_checked()){
                HashMap<String, Object> language_hashmap = new HashMap<>();
                language_hashmap.put("language", language_info.getLanguage_name());
                send_analytics_event("language_selected_render",language_hashmap);
            }
        }
        if(arabic_language != null){
            StringBuilder arabic_verses_string_builder = new StringBuilder();
            for(Text_item text_item : arabic_language.getArrayList_of_all_of_the_translations()){
                arabic_verses_string_builder.append(text_item).append(' ');
            }
            int arabic_letters = countArabicLetters(arabic_verses_string_builder.toString());
            event_hashmap.put("arabic_letters", arabic_letters);
        } else {
            event_hashmap.put("arabic_letters", 0);
        }
        send_analytics_event("render_started", event_hashmap);
    }

    private void check_if_this_is_the_first_launch_and_send_an_event_if_so() {
        String first_time_app_open_prefs = "app_opened_for_the_first_time";
        Preferences prefs = Preferences.userRoot().node("sabrly");
        boolean first_time_app_opened = prefs.getBoolean(first_time_app_open_prefs, true);
        if (first_time_app_opened) {
            send_analytics_event("first_time_launch");
            prefs.putBoolean(first_time_app_open_prefs, false);
        }
    }

    public int countArabicLetters(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        // Normalize so diacritics become separate code points
        String s = Normalizer.normalize(input, Normalizer.Form.NFD);

        int count = 0;

        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);

            // Skip tashkeel & Quranic marks (all combining marks)
            int type = Character.getType(cp);
            if (type == Character.NON_SPACING_MARK
                    || type == Character.COMBINING_SPACING_MARK
                    || type == Character.ENCLOSING_MARK) {
                continue;
            }

            // Skip tatweel
            if (cp == 0x0640) continue;

            // Count Arabic letters only
            if (isArabicLetter(cp)) {
                count++;
            }
        }

        return count;
    }

    private boolean isArabicLetter(int cp) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(cp);

        if (block == Character.UnicodeBlock.ARABIC
                || block == Character.UnicodeBlock.ARABIC_SUPPLEMENT
                || block == Character.UnicodeBlock.ARABIC_EXTENDED_A
                || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A
                || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) {

            return Character.isLetter(cp);
        }
        return false;
    }

    private boolean has_enough_time_passed_to_send_an_analytics_event(String analytics_name) {
        if (analytics_cool_down_hashmap.containsKey(analytics_name)) {
            if (System.currentTimeMillis() - analytics_cool_down_hashmap.get(analytics_name) >= TimeUnit.SECONDS.toMillis(5)) {
                analytics_cool_down_hashmap.put(analytics_name, System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        } else {
            analytics_cool_down_hashmap.put(analytics_name, System.currentTimeMillis());
            return true;
        }
    }

    private void split_the_verse_time_line(HelloController helloController) {
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        long polygon_position_in_nano_seconds = pixels_to_nanoseconds(time_line_pane_data, return_polygon_middle_position(time_line_pane_data) - time_line_pane_data.getTime_line_base_line());
        long original_verse_start = ayats_processed.get(selected_verse).getStart_millisecond();
        long original_verse_end = ayats_processed.get(selected_verse).getStart_millisecond() + ayats_processed.get(selected_verse).getDuration();
        long original_verse_new_width_in_nano_seconds = polygon_position_in_nano_seconds - original_verse_start;
        double original_verse_new_width_in_pixels = nanoseconds_to_pixels(time_line_pane_data, original_verse_new_width_in_nano_seconds);
        double created_verse_width = nanoseconds_to_pixels(time_line_pane_data, original_verse_end - polygon_position_in_nano_seconds);
        StackPane main_verse_stack_pane = ayats_processed.get(selected_verse).getStack_pane_hosting_rectangle();
        Rectangle main_verse_rectangle = (Rectangle) main_verse_stack_pane.getChildren().getFirst();
        ayats_processed.get(selected_verse).setDuration(polygon_position_in_nano_seconds - ayats_processed.get(selected_verse).getStart_millisecond());
        main_verse_stack_pane.setMinWidth(original_verse_new_width_in_pixels);
        main_verse_stack_pane.setPrefWidth(original_verse_new_width_in_pixels);
        main_verse_stack_pane.setMaxWidth(original_verse_new_width_in_pixels);
        main_verse_rectangle.setWidth(original_verse_new_width_in_pixels);
        StackPane new_stack_pane_with_new_verse = return_the_stack_pane_for_the_verse_rectangle(ayats_processed.get(selected_verse).getVerse_number() - ayats_processed.getFirst().getVerse_number(), return_polygon_middle_position(time_line_pane_data), created_verse_width);
        Rectangle new_verse_rectangle = (Rectangle) new_stack_pane_with_new_verse.getChildren().getFirst();
        helloController.time_line_pane.getChildren().add(new_stack_pane_with_new_verse);
        ayats_processed.add(selected_verse + 1, new Verse_class_final(ayats_processed.get(selected_verse).getVerse_number(), original_verse_end - polygon_position_in_nano_seconds, polygon_position_in_nano_seconds, new_stack_pane_with_new_verse));
        rename_the_verse_rectangle_of_the_same_verse();
        duplicate_the_text_item_of_every_language(helloController, selected_verse);
        StackPane empty_stackPane = new StackPane();
        Rectangle empty_rectangle = new Rectangle();
        if (sound_mode == Sound_mode.CHOSEN) {
            {
                StackPane previous_stack_pane = return_prev_stack_pane(ayats_processed.get(selected_verse));
                if (previous_stack_pane.getChildren().isEmpty()) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse, main_verse_stack_pane, main_verse_rectangle, empty_stackPane, empty_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, Verse_position_mode.START);
                } else {
                    Rectangle previous_rectangle = (Rectangle) previous_stack_pane.getChildren().getFirst();
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse, main_verse_stack_pane, main_verse_rectangle, previous_stack_pane, previous_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, Verse_position_mode.MIDDLE);
                }
            }
            {
                StackPane next_stack_pane = return_next_stack_pane(ayats_processed.get(selected_verse + 1));
                if (next_stack_pane.getChildren().isEmpty()) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 1, new_stack_pane_with_new_verse, new_verse_rectangle, main_verse_stack_pane, main_verse_rectangle, empty_stackPane, empty_rectangle, Verse_position_mode.END);
                } else {
                    Rectangle next_rectangle = (Rectangle) next_stack_pane.getChildren().getFirst();
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 1, new_stack_pane_with_new_verse, new_verse_rectangle, main_verse_stack_pane, main_verse_rectangle, next_stack_pane, next_rectangle, Verse_position_mode.MIDDLE);
                    if (selected_verse + 2 < ayats_processed.size()) {
                        StackPane next_next_stack_pane = return_next_stack_pane(ayats_processed.get(selected_verse + 2));
                        if (next_next_stack_pane.getChildren().isEmpty()) {
                            listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 2, next_stack_pane, next_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, empty_stackPane, empty_rectangle, Verse_position_mode.END);
                        } else {
                            Rectangle next_next_rectangle = (Rectangle) next_next_stack_pane.getChildren().getFirst();
                            listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 2, next_stack_pane, next_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, next_next_stack_pane, next_next_rectangle, Verse_position_mode.MIDDLE);
                        }
                    }
                }
            }
        } else if (sound_mode == Sound_mode.UPLOADED) {
            {
                StackPane previous_stack_pane = return_prev_stack_pane_uploaded_sound(ayats_processed.get(selected_verse));
                if (previous_stack_pane.getChildren().isEmpty()) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse, main_verse_stack_pane, main_verse_rectangle, empty_stackPane, empty_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, Verse_position_mode.START);
                } else {
                    Rectangle previous_rectangle = (Rectangle) previous_stack_pane.getChildren().getFirst();
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse, main_verse_stack_pane, main_verse_rectangle, previous_stack_pane, previous_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, Verse_position_mode.MIDDLE);
                }
            }
            {
                StackPane next_stack_pane = return_next_stack_pane_uploaded_sound(ayats_processed.get(selected_verse + 1));
                if (next_stack_pane.getChildren().isEmpty()) {
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 1, new_stack_pane_with_new_verse, new_verse_rectangle, main_verse_stack_pane, main_verse_rectangle, empty_stackPane, empty_rectangle, Verse_position_mode.END);
                } else {
                    Rectangle next_rectangle = (Rectangle) next_stack_pane.getChildren().getFirst();
                    listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 1, new_stack_pane_with_new_verse, new_verse_rectangle, main_verse_stack_pane, main_verse_rectangle, next_stack_pane, next_rectangle, Verse_position_mode.MIDDLE);
                    if (selected_verse + 2 < ayats_processed.size()) {
                        StackPane next_next_stack_pane = return_next_stack_pane_uploaded_sound(ayats_processed.get(selected_verse + 2));
                        if (next_next_stack_pane.getChildren().isEmpty()) {
                            listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 2, next_stack_pane, next_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, empty_stackPane, empty_rectangle, Verse_position_mode.END);
                        } else {
                            Rectangle next_next_rectangle = (Rectangle) next_next_stack_pane.getChildren().getFirst();
                            listen_to_mouse_moved_inside_rectangle(helloController, time_line_pane_data, selected_verse + 2, next_stack_pane, next_rectangle, new_stack_pane_with_new_verse, new_verse_rectangle, next_next_stack_pane, next_next_rectangle, Verse_position_mode.MIDDLE);
                        }
                    }
                }
            }
        }
    }

    private void rename_the_verse_rectangle_of_the_same_verse() {
        HashMap<Integer, ArrayList<Verse_class_final>> hashMap_with_verse_class_final_tied_to_verse_number = new HashMap<>();
        for (int i = 0; i < ayats_processed.size(); i++) {
            ArrayList<Verse_class_final> local_array_list = hashMap_with_verse_class_final_tied_to_verse_number.getOrDefault(ayats_processed.get(i).getVerse_number(), new ArrayList<>());
            local_array_list.add(ayats_processed.get(i));
            hashMap_with_verse_class_final_tied_to_verse_number.put(ayats_processed.get(i).getVerse_number(), local_array_list);
        }
        for (int key : hashMap_with_verse_class_final_tied_to_verse_number.keySet()) {
            ArrayList<Verse_class_final> value = hashMap_with_verse_class_final_tied_to_verse_number.get(key);
            if (value.size() == 1) {
                continue;
            }
            for (int i = 0; i < value.size(); i++) {
                Label verse_text = (Label) value.get(i).getStack_pane_hosting_rectangle().getChildren().get(1);
                verse_text.setText("Verse ".concat(String.valueOf(key)).concat(" - ").concat(String.valueOf(i + 1)));
                value.get(i).getStack_pane_hosting_rectangle().getChildren().set(1, verse_text);
            }
        }
    }

    private StackPane return_prev_stack_pane(Verse_class_final verse_class_final) {
        for (int i = 0; i < ayats_processed.size(); i++) {
            if (ayats_processed.get(i) == verse_class_final) {
                if (i == 0 || ayats_processed.get(i - 1).getVerse_number() != ayats_processed.get(i).getVerse_number()) {
                    return new StackPane();
                } else {
                    return ayats_processed.get(i - 1).getStack_pane_hosting_rectangle();
                }
            }
        }
        return new StackPane();
    }

    private StackPane return_next_stack_pane(Verse_class_final verse_class_final) {
        for (int i = 0; i < ayats_processed.size(); i++) {
            if (ayats_processed.get(i) == verse_class_final) {
                if (i == ayats_processed.size() - 1 || ayats_processed.get(i + 1).getVerse_number() != ayats_processed.get(i).getVerse_number()) {
                    return new StackPane();
                } else {
                    return ayats_processed.get(i + 1).getStack_pane_hosting_rectangle();
                }
            }
        }
        return new StackPane();
    }

    private StackPane return_prev_stack_pane_uploaded_sound(Verse_class_final verse_class_final) {
        for (int i = 0; i < ayats_processed.size(); i++) {
            if (ayats_processed.get(i) == verse_class_final) {
                if (i == 0) {
                    return new StackPane();
                } else {
                    return ayats_processed.get(i - 1).getStack_pane_hosting_rectangle();
                }
            }
        }
        return new StackPane();
    }

    private StackPane return_next_stack_pane_uploaded_sound(Verse_class_final verse_class_final) {
        for (int i = 0; i < ayats_processed.size(); i++) {
            if (ayats_processed.get(i) == verse_class_final) {
                if (i == ayats_processed.size() - 1) {
                    return new StackPane();
                } else {
                    return ayats_processed.get(i + 1).getStack_pane_hosting_rectangle();
                }
            }
        }
        return new StackPane();
    }

    private void duplicate_the_text_item_of_every_language(HelloController helloController, int index) {
        ObservableList<Language_info> all_of_the_language_info_items = helloController.list_view_with_all_of_the_languages.getItems();
        for (int i = 0; i < all_of_the_language_info_items.size(); i++) {
            ArrayList<Text_item> local = all_of_the_language_info_items.get(i).getArrayList_of_all_of_the_translations();
            local.add(index, new Text_item(local.get(index)));
        }
    }

    private void listen_to_mouse_leaving_and_entering_time_line(HelloController helloController) {
        helloController.parent_stack_pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Insets margin = VBox.getMargin(helloController.h_box_bottom_border_pane_time_line);
                double pane_height = helloController.show_the_result_screen.getBottom().getLayoutBounds().getHeight();
                Point2D bottom_coordinates = helloController.show_the_result_screen.getBottom().sceneToLocal(mouseEvent.getX(), mouseEvent.getY());
                if (bottom_coordinates.getY() >= margin.getTop() && bottom_coordinates.getY() <= pane_height - margin.getBottom() && helloController.show_the_result_screen.getBottom().contains(bottom_coordinates)) {
                    helloController.split_verse_button.setVisible(true);
                } else {
                    helloController.split_verse_button.setVisible(false);
                }
            }
        });
    }

    private boolean is_language_right_to_left(String name_of_language) {
        if (name_of_language.equals("arabic") ||
                name_of_language.equals("hebrew") ||
                name_of_language.equals("persian") ||
                name_of_language.equals("dari") ||
                name_of_language.equals("urdu") ||
                name_of_language.equals("pashto") ||
                name_of_language.equals("sindhi") ||
                name_of_language.equals("kurdish (sorani)") ||
                name_of_language.equals("uyghur") ||
                name_of_language.equals("divehi")
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void show_a_dialogue_asking_user_to_choose_video_render_save_location(HelloController helloController) {
        if (render_video_dialogue_stage != null && render_video_dialogue_stage.isShowing()) {
            render_video_dialogue_stage.toFront();
            return;
        }
        render_video_dialogue_stage = new Stage();
        render_video_dialogue_stage.initOwner(main_stage);
        render_video_dialogue_stage.initStyle(StageStyle.DECORATED);

        String previous_file_name = get_the_file_name_render_engine();
        String previous_file_location = get_the_file_location_name_render_engine();


        StackPane stackPane = new StackPane();
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        Label choose_where_to_save_the_output_label = new Label("Choose a directory where the output will be saved");
        VBox.setMargin(choose_where_to_save_the_output_label, new Insets(0, 10, 0, 10));

        HBox file_name_h_box = new HBox(10);
        file_name_h_box.setAlignment(Pos.CENTER);
        VBox.setMargin(file_name_h_box, new Insets(0, 10, 0, 10));
        StackPane stack_pane_holding_label_name = new StackPane();
        Label label_saying_file_name = new Label("File name: ");
        Label fake_label_saying_file_location = new Label("File location: ");
        fake_label_saying_file_location.setVisible(false);
        TextField file_name_text_field = new TextField();
        file_name_text_field.setText(previous_file_name);
        JFXButton fake_browse_button = new JFXButton("Browse");
        fake_browse_button.setVisible(false);
        stack_pane_holding_label_name.getChildren().addAll(label_saying_file_name, fake_label_saying_file_location);
        file_name_h_box.getChildren().addAll(stack_pane_holding_label_name, file_name_text_field, fake_browse_button);

        HBox file_location_h_box = new HBox(10);
        file_location_h_box.setAlignment(Pos.CENTER);
        VBox.setMargin(file_location_h_box, new Insets(0, 10, 0, 10));
        StackPane stack_pane_holding_label_location = new StackPane();
        Label label_saying_file_location = new Label("File location: ");
        Label fake_label_saying_file_name = new Label("File name: ");
        fake_label_saying_file_name.setVisible(false);
        TextField file_location_text_field = new TextField();
        file_location_text_field.setText(previous_file_location);
        file_location_text_field.setEditable(false);
        JFXButton browse_button = new JFXButton("Browse");
        stack_pane_holding_label_location.getChildren().addAll(label_saying_file_location, fake_label_saying_file_name);
        file_location_h_box.getChildren().addAll(stack_pane_holding_label_location, file_location_text_field, browse_button);

        JFXButton render_button = new JFXButton();
        render_button.setText("Render");
        render_button.setStyle("-fx-font-size: 14px;");
        StackPane.setAlignment(render_button, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(render_button, new Insets(0, 10, 10, 0));

        if (render_video_location() != null) {
            file_location_text_field.setText(render_video_location());
        }

        browse_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                send_analytics_event("render_browse_button_clicked");
                File chosen_directory = open_the_file_chooser_to_select_a_dialog();
                if (chosen_directory != null) {
                    file_location_text_field.setText(chosen_directory.getAbsolutePath());
                    file_location_text_field.end();
                }
            }
        });

        /*file_name_text_field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String old_value, String new_value) {
                if(new_value.trim().isEmpty()){
                    render_button.setDisable(true);
                } else {
                    if(!file_location_text_field.getText().isEmpty()){
                        render_button.setDisable(false);
                    }
                }
            }
        });

        file_location_text_field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String old_value, String new_value) {
                if(new_value.isEmpty()){
                    render_button.setDisable(true);
                } else {
                    if(!file_name_text_field.getText().trim().isEmpty()){
                        render_button.setDisable(false);
                    }
                }
            }
        });*/

        render_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                send_analytics_event("analytics_button_clicked");
                if (!is_this_a_valid_file_name(file_name_text_field.getText())) {
                    showToast(render_video_dialogue_stage, "File name not valid", 3000);
                    return;
                }
                if (file_name_text_field.getText().trim().isEmpty()) {
                    showToast(render_video_dialogue_stage, "File name can't be empty", 3000);
                    return;
                }
                if (file_location_text_field.getText().trim().isEmpty()) {
                    showToast(render_video_dialogue_stage, "File location can't be empty", 3000);
                    return;
                }
                save_the_name_and_location(file_name_text_field.getText(), file_location_text_field.getText());
                send_render_analytics_event(helloController);
                start_the_rendering_engine(helloController, file_name_text_field.getText(), file_location_text_field.getText());
            }
        });


        vBox.getChildren().addAll(choose_where_to_save_the_output_label, file_name_h_box, file_location_h_box);

        stackPane.getChildren().addAll(vBox, render_button);
        Scene scene = new Scene(stackPane, 450, 225);
        render_video_dialogue_stage.setScene(scene);
        render_video_dialogue_stage.sizeToScene();
        render_video_dialogue_stage.setTitle("Choose a directory");
        render_video_dialogue_stage.show();
    }

    private File open_the_file_chooser_to_select_a_dialog() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a directory for the output to be saved in");
        return chooser.showDialog(new Stage());
    }

    private String render_video_location() {
        Preferences prefs = Preferences.userRoot().node("sabrly");
        String file_path = prefs.get("render_directory", null);
        if (file_path == null || !Files.exists(Paths.get(file_path))) {
            return null;
        } else {
            return file_path;
        }
    }

    private boolean is_this_a_valid_file_name(String file_name) {
        return !file_name.matches(".*[\\\\/:*?\"<>|.].*");
    }

    private void start_the_rendering_engine(HelloController helloController, String file_name, String file_location) {
        helloController.show_logo_loading_screen.setVisible(true);
        helloController.show_the_result_screen_stack_pane.setVisible(false);

        helloController.progress_indicator_first_loading_screen.setVisible(false);
        helloController.vbox_holding_rendering_bar_and_label_render_engine_progress.setVisible(true);
        render_video_dialogue_stage.close();

        Path base_path = get_os_base_path();
        FFmpegLogCallback.set();
        Path file_path = Paths.get(file_location, file_name);
        Time_line_pane_data time_line_pane_data = (Time_line_pane_data) helloController.time_line_pane.getUserData();
        final int frames_per_second = 30;
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file_path.toString().concat(".mp4"), Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height);
        FFmpegFrameGrabber audioGrabber;
        if (sound_mode == Sound_mode.CHOSEN) {
            audioGrabber = new FFmpegFrameGrabber(base_path.resolve("temp/sound/combined.wav").toString());
        } else { //sound_mode == Sound_mode.UPLOADED
            audioGrabber = new FFmpegFrameGrabber(base_path.resolve("temp/sound/converted.wav").toString());
        }
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(frames_per_second);
        recorder.setVideoOption("crf", "0");
        recorder.setVideoOption("preset", "veryslow");
        recorder.setVideoOption("level", "5.1");
        recorder.setGopSize(frames_per_second);

        recorder.setVideoBitrate(20_000_000);

        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.setSampleRate(44100);
        recorder.setAudioChannels(2);
        recorder.setAudioBitrate(192000);

        Canvas canvas_to_be_used_for_buffer_image = new Canvas(Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height);

        long number_of_frames = (get_duration() * frames_per_second) / 1_000_000_000L;
        BlockingQueue<ArrayList<BufferedImage>> buffered_image_blocking_queue = new LinkedBlockingQueue<>();
        AtomicInteger current_frame_number = new AtomicInteger(0);
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(Duration.seconds(0),
                event -> {
                    if (current_frame_number.get() >= number_of_frames) {
                        timeline.stop();
                        return;
                    }
                    long time_in_nanoseconds = (current_frame_number.get() * 1_000_000_000L) / frames_per_second;
                    ArrayList<BufferedImage> buffered_image_array_list = new ArrayList<>();
                    for (Language_info language_info : helloController.list_view_with_all_of_the_languages.getItems()) {
                        if (language_info.isVisible_check_mark_checked()) {
                            place_the_canvas_text(helloController, canvas_to_be_used_for_buffer_image, time_in_nanoseconds);
                            buffered_image_array_list.add(get_buffered_image_from_canvas(canvas_to_be_used_for_buffer_image));
                        }
                    }
                    try {
                        buffered_image_blocking_queue.put(buffered_image_array_list);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    current_frame_number.incrementAndGet();
                });
        timeline.getKeyFrames().addAll(kf, new KeyFrame(Duration.millis(100)));
        timeline.setCycleCount(Timeline.INDEFINITE); // run forever
        timeline.play();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    audioGrabber.start();
                    recorder.start();
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    Frame nextAudioFrame = audioGrabber.grabSamples();
                    int processed_frame = 0;
                    while (current_frame_number.get() < number_of_frames) {
                        if (buffered_image_blocking_queue.isEmpty()) {
                            continue;
                        }
                        if (processed_frame % (frames_per_second / 10) == 0) {
                            final int finalProcessed_frame = processed_frame;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    double progress = ((double) finalProcessed_frame) / number_of_frames;
                                    if (progress > helloController.video_render_progress_bar.getProgress()) {
                                        helloController.video_render_progress_bar.setProgress(progress);
                                        helloController.label_for_percentage_rendering_engine.setText(String.valueOf((int) (progress * 100)).concat("%"));
                                    }
                                }
                            });
                        }
                        BufferedImage root_buffered_image = new BufferedImage(Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height, BufferedImage.TYPE_INT_ARGB);
                        long time_in_nanoseconds = (processed_frame * 1_000_000_000L) / frames_per_second;
                        long time_in_milliseconds = TimeUnit.NANOSECONDS.toMillis(time_in_nanoseconds);
                        long time_in_microseconds = TimeUnit.NANOSECONDS.toMicros(time_in_nanoseconds);
                        String image_id = return_the_image_on_click(helloController.time_line_pane, nanoseconds_to_pixels(time_line_pane_data, time_in_nanoseconds) + time_line_pane_data.getTime_line_base_line());
                        if (!image_id.equals(no_image_found)) {
                            Media_pool media_pool = hashMap_with_media_pool_items.get(image_id);
                            Path path = Paths.get(base_path.resolve("temp/images/base").toString(), image_id.concat(".raw"));
                            Image image = readRawImage(path.toString(), media_pool.getWidth(), media_pool.getHeight());
                            BufferedImage bufferedImage = image_to_buffered_image(image);
                            add_buffer_image_to_root_buffer_image(root_buffered_image, bufferedImage);
                        }
                        Shape_object_time_line shape_object_time_line = return_the_shape_on_click(helloController.time_line_pane, nanoseconds_to_pixels(time_line_pane_data, time_in_nanoseconds) + time_line_pane_data.getTime_line_base_line());
                        if (shape_object_time_line != null) {
                            double time_difference_compared_to_start = time_in_nanoseconds - shape_object_time_line.getStart_time();
                            double time_difference_compared_to_end = shape_object_time_line.getEnd_time() - time_in_nanoseconds;
                            double opacity = shape_object_time_line.getOpacity_settings().return_total_opacity(time_difference_compared_to_start, time_difference_compared_to_end);
                            if (opacity > 0) {
                                add_buffer_image_to_root_buffer_image(root_buffered_image, blacked_out_image_four_k, (float) opacity);
                            }
                        }
                        ArrayList<BufferedImage> buffered_images_for_this_frame = buffered_image_blocking_queue.take();
                        for (int i = 0; i < buffered_images_for_this_frame.size(); i++) {
                            add_buffer_image_to_root_buffer_image(root_buffered_image, buffered_images_for_this_frame.get(i));
                        }
                        BufferedImage bgr_buffered_image = new BufferedImage(root_buffered_image.getWidth(), root_buffered_image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                        add_buffer_image_to_root_buffer_image(bgr_buffered_image, root_buffered_image);
                        Frame current_frame = converter.convert(bgr_buffered_image);
                        recorder.setTimestamp(time_in_microseconds);
                        recorder.record(current_frame);
                        while (nextAudioFrame != null && audioGrabber.getTimestamp() <= time_in_microseconds) {
                            recorder.record(nextAudioFrame);
                            nextAudioFrame = audioGrabber.grabSamples();
                        }
                        processed_frame++;
                    }
                    while (nextAudioFrame != null) {
                        recorder.record(nextAudioFrame);
                        nextAudioFrame = audioGrabber.grabSamples();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            helloController.video_render_progress_bar.setProgress(1.0);
                            helloController.label_for_percentage_rendering_engine.setText("100%");
                        }
                    });
                    audioGrabber.stop();
                    recorder.stop();
                    audioGrabber.release();
                    recorder.release();
                } catch (Exception e) {
                    System.err.println("The rendering engine ran into a problem. " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            show_alert("The rendering engine has ran into a problem. Please try again later.");
                        }
                    });
                }
            }
        });
        executor.shutdown();
    }

    private BufferedImage get_buffered_image_from_canvas(Canvas canvas) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return image_to_buffered_image(canvas.snapshot(params, null));
    }

    private void add_buffer_image_to_root_buffer_image(BufferedImage original_buffered_image, BufferedImage buffered_image_to_be_added) {
        add_buffer_image_to_root_buffer_image(original_buffered_image, buffered_image_to_be_added, 1F);
    }

    private void add_buffer_image_to_root_buffer_image(BufferedImage original_buffered_image, BufferedImage buffered_image_to_be_added, float opacity) {
        Graphics2D graphics2D = original_buffered_image.createGraphics();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        graphics2D.drawImage(buffered_image_to_be_added, 0, 0, Global_default_values.translation_canvas_width, Global_default_values.translation_canvas_height, null);
        graphics2D.dispose();
    }

    private void save_the_name_and_location(String file_name, String file_location) {
        Preferences prefs = Preferences.userRoot().node("sabrly");
        prefs.put("sabrly_render_file_name", file_name);
        prefs.put("sabrly_render_file_location", file_location);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private String get_the_file_name_render_engine() {
        Preferences prefs = Preferences.userRoot().node("sabrly");
        return prefs.get("sabrly_render_file_name", "untitled");
    }

    private String get_the_file_location_name_render_engine() {
        Preferences prefs = Preferences.userRoot().node("sabrly");
        String file_location = prefs.get("sabrly_render_file_location", "");
        Path path = Paths.get(file_location);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            file_location = "";
            prefs.put("sabrly_render_file_location", file_location);
        }
        return file_location;
    }
    private void add_app_icon_to_title_bar(Stage stage){
        if(!is_this_a_mac_device()){
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/Sabrly_mini.png")));
        }
    }
}