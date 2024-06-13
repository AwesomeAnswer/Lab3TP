package com.example.spacegame;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@Controller
@RequestMapping("/")
public class GameController {

    private final Game game;
    private final ObjectMapper objectMapper;
    private final Schema schema;

    public GameController(Game game, ObjectMapper objectMapper) throws Exception {
        this.game = game;
        this.objectMapper = objectMapper;
        this.schema = loadSchema();
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/operator")
    public String operator() {
        return "operator";
    }

    @GetMapping("/ship")
    public String ship() {
        return "ship";
    }

    @MessageMapping("/move")
    @SendTo("/topic/ship")
    public Ship moveShip(Game.MoveMessage message) {
        Ship ship = game.getShip();
        game.updateShipPosition(ship.getX() + message.getDx(), ship.getY() + message.getDy(), ship.getRotation());
        return game.getShip();
    }

    @MessageMapping("/rotate")
    @SendTo("/topic/ship")
    public Ship rotateShip(Game.RotateMessage message) {
        Ship ship = game.getShip();
        game.updateShipPosition(ship.getX(), ship.getY(), (ship.getRotation() + message.getDr()) % 360);
        return game.getShip();
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Operator sendMessage(Operator message) {
        return message;
    }

    @MessageMapping("/station")
    @SendTo("/topic/station")
    public Station getStation() {
        return game.getStation();
    }

    @GetMapping("/export")
    @ResponseBody
    public Game.GameState exportState() {
        return game.exportState();
    }

    @PostMapping("/import")
    @ResponseBody
    public String importState(@RequestBody Game.GameState state) {
        try {
            validateState(state);
            game.importState(state);
            return "Import successful";
        } catch (Exception e) {
            return "Import failed: " + e.getMessage();
        }
    }

    private void validateState(Game.GameState state) throws Exception {
        String stateJsonString = objectMapper.writeValueAsString(state);
        JSONObject stateJson = new JSONObject(stateJsonString);

        schema.validate(stateJson);
    }

    private Schema loadSchema() throws Exception {
        InputStream schemaStream = new ClassPathResource("game-state-schema.json").getInputStream();
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
        return SchemaLoader.load(rawSchema);
    }
}
