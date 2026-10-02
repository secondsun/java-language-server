package dev.secondsun.lsp;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class RegistrationParams {
    public String id, method;
    public JsonElement registerOptions;
    public List<Registration> registrations = new ArrayList<>();

    public static class Registration {
        public String id, method;
        public JsonElement registerOptions;
    }
}
