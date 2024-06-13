package com.example.spacegame;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

@Component
public class Game {
    private Ship ship = new Ship();
    private Station station = new Station();

    @PostConstruct
    public void init() {
        resetGame();
    }

    public void resetGame() {
        Random random = new Random();
        ship.setX(random.nextInt(500) - 250);
        ship.setY(random.nextInt(300) - 150);
        ship.setRotation(random.nextInt(360));

        station.setX(random.nextInt(500) - 250);
        station.setY(random.nextInt(300) - 150);
        station.setRotation(random.nextInt(360));
    }

    public Ship getShip() {
        return ship;
    }

    public Station getStation() {
        return station;
    }

    public void updateShipPosition(double x, double y, double rotation) {
        ship.setX(x);
        ship.setY(y);
        ship.setRotation(rotation);
        checkDocking(ship, station);
    }

    private void checkDocking(Ship ship, Station station) {
        if (Math.abs(ship.getX() - station.getX()) < 10 &&
                Math.abs(ship.getY() - station.getY()) < 10 &&
                Math.abs(ship.getRotation() - station.getRotation()) < 5) {
            ship.setDocked(true);
        }
    }

    public GameState exportState() {
        return new GameState(ship, station);
    }

    public void importState(GameState state) {
        ship = state.getShip();
        station = state.getStation();
    }

    public static class MoveMessage {
        private double dx;
        private double dy;

        public double getDx() {
            return dx;
        }

        public void setDx(double dx) {
            this.dx = dx;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }
    }

    public static class RotateMessage {
        private double dr;

        public double getDr() {
            return dr;
        }

        public void setDr(double dr) {
            this.dr = dr;
        }
    }

    public static class GameState {
        private Ship ship;
        private Station station;

        public GameState() {
        }

        public GameState(Ship ship, Station station) {
            this.ship = ship;
            this.station = station;
        }

        public Ship getShip() {
            return ship;
        }

        public void setShip(Ship ship) {
            this.ship = ship;
        }

        public Station getStation() {
            return station;
        }

        public void setStation(Station station) {
            this.station = station;
        }
    }
}
