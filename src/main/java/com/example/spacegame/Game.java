package com.example.spacegame;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

@Component
public class Game {
    private AtomicReference<Ship> ship = new AtomicReference<>(new Ship());
    private AtomicReference<Station> station = new AtomicReference<>(new Station());

    @PostConstruct
    public void init() {
        resetGame();
    }

    public void resetGame() {
        Random random = new Random();
        ship.get().setX(random.nextInt(500) - 250);
        ship.get().setY(random.nextInt(300) - 150);
        ship.get().setRotation(random.nextInt(360));

        station.get().setX(random.nextInt(500) - 250);
        station.get().setY(random.nextInt(300) - 150);
        station.get().setRotation(random.nextInt(360));
    }

    public Ship getShip() {
        return ship.get();
    }

    public Station getStation() {
        return station.get();
    }

    public void updateShipPosition(double x, double y, double rotation) {
        ship.updateAndGet(s -> {
            s.setX(x);
            s.setY(y);
            s.setRotation(rotation);
            checkDocking(s, station.get());
            return s;
        });
    }

    private void checkDocking(Ship ship, Station station) {
        if (Math.abs(ship.getX() - station.getX()) < 10 &&
                Math.abs(ship.getY() - station.getY()) < 10 &&
                Math.abs(ship.getRotation() - station.getRotation()) < 5) {
            ship.setDocked(true);
        }
    }

    public GameState exportState() {
        return new GameState(ship.get(), station.get());
    }

    public void importState(GameState state) throws IllegalArgumentException {
        validateState(state);
        ship.set(state.getShip());
        station.set(state.getStation());
    }

    private void validateState(GameState state) throws IllegalArgumentException {
        if (state.getShip() == null || state.getStation() == null) {
            throw new IllegalArgumentException("Invalid game state: ship or station is null");
        }
        validateCoordinates(state.getShip().getX(), state.getShip().getY(), state.getShip().getRotation());
        validateCoordinates(state.getStation().getX(), state.getStation().getY(), state.getStation().getRotation());
    }

    private void validateCoordinates(double x, double y, double rotation) throws IllegalArgumentException {
        if (x < -250 || x > 250) {
            throw new IllegalArgumentException("Invalid coordinate: x must be between -250 and 250");
        }
        if (y < -150 || y > 150) {
            throw new IllegalArgumentException("Invalid coordinate: y must be between -150 and 150");
        }
        if (rotation < 0 || rotation >= 360) {
            throw new IllegalArgumentException("Invalid rotation: rotation must be between 0 and 359");
        }
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
