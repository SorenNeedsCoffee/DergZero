package fyi.sorenneedscoffee.derg_zero.xp.data.implementations;

import com.google.gson.*;
import fyi.sorenneedscoffee.derg_zero.xp.data.RoleDataContext;
import fyi.sorenneedscoffee.derg_zero.xp.data.models.LevelRole;
import fyi.sorenneedscoffee.derg_zero.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.RemoveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.RetrieveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.SaveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.UpdateListRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class JSONRoleDataContext implements RoleDataContext {
    private final File file;
    private Gson gson;
    private JsonObject data;
    private boolean beautify = false;

    public JSONRoleDataContext(File file) {
        this.file = file;
        loadData();
    }

    public JSONRoleDataContext(File file, boolean beautify) {
        this(file);

        this.beautify = beautify;
    }

    public File getFile() {
        return file;
    }

    public JsonObject getData() {
        return data;
    }

    public boolean isBeautify() {
        return beautify;
    }

    private void buildGson() {
        GsonBuilder builder = new GsonBuilder();

        if (beautify) {
            builder.setPrettyPrinting();
        }

        gson = builder.create();
    }

    private void loadData() {
        if (!file.exists()) {
            data = new JsonObject();
            return;
        }

        String contents = readFile();
        data = (JsonObject) JsonParser.parseString(contents);
    }

    private String readFile() {
        try {
            Scanner myReader = new Scanner(file);
            String contents = "";

            while (myReader.hasNextLine()) {
                contents = contents.concat(myReader.nextLine() + "\n");
            }

            myReader.close();
            return contents;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void saveData() {
        if (gson == null) {
            buildGson();
        }

        String contents = gson.toJson(data);

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(contents);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveListObject(String groupId, LevelRoleList list) {
        JsonObject obj = new JsonObject();
        data.add(groupId, obj);

        JsonArray array = new JsonArray();
        for (LevelRole role : list) {
            JsonObject roleObj = new JsonObject();
            roleObj.addProperty("level", role.getLevel());
            roleObj.addProperty("roleId", role.getRoleID());
            array.add(roleObj);
        }
        obj.add("roles", array);

        saveData();
    }

    private JsonArray getListObject(String groupId) {
        if (data.has(groupId))
            return data.getAsJsonObject(groupId).getAsJsonArray("roles");

        return null;
    }

    private void removeListObject(String groupId) {
        data.remove(groupId);

        saveData();
    }


    @Override
    public void saveList(SaveListRequest request) {
        saveListObject(request.getGroupId(), request.getList());
    }

    @Override
    public LevelRoleList retrieveList(RetrieveListRequest request) {
        JsonArray roles = getListObject(request.getGroupId());
        if (roles == null)
            return null;
        LevelRoleList result = new LevelRoleList();

        for (JsonElement element : roles) {
            JsonObject roleObj = element.getAsJsonObject();
            LevelRole role = new LevelRole(roleObj.get("level").getAsInt(), roleObj.get("roleId").getAsString());
            result.add(role);
        }

        return result;
    }

    @Override
    public void updateList(UpdateListRequest request) {
        saveListObject(request.getGroupId(), request.getList());
    }

    @Override
    public void removeList(RemoveListRequest request) {
        removeListObject(request.getGroupId());
    }
}
