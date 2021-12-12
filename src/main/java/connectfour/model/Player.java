package connectfour.model;

public enum Player {

    HUMAN(1),
    COMPUTER(2);

    Player(int id){
        this.id = id;
    }

    public final int id;
}
