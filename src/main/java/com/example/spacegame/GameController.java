package com.example.spacegame;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class GameController {

private final Game game;

    public GameController(Game game) {
        this.game = game;
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
            game.importState(state);
            return "Import successful";
        } catch (IllegalArgumentException e) {
            return "Import failed: " + e.getMessage();
        }
    }
}
