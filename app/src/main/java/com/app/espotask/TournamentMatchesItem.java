package com.app.espotask;

public class TournamentMatchesItem {
    private int id; // Maps to m_id
    private int imageResource;
    private String banner;
    private String title;
    private String time;
    private int prizePool;
    private int perKill;
    private int entryFee;
    private String type;
    private String version;
    private String map;
    private String slots;
    private int filledPositions;
    private int noOfPlayer;
    private String entryType;
    private int entryFeeCoins;
    private int entryFeeTickets;

    private int matchStatus; // 👈 NEW FIELD
    private String matchUrl; // 👈 New
    private String userJoined;

    public TournamentMatchesItem(int id, String imageUrl, String title, String time,
                                 int entryFeeCoins, int entryFeeTickets, String entryType,
                                 int prizePool, int perKill, String type, String version,
                                 String map, String slots, int filledPositions, int noOfPlayer,
                                 int matchStatus, String matchUrl, String userJoined) { // 👈 ADD PARAMETER
        this.id = id;
        this.imageResource = R.drawable.ic_freefire_banner;
        this.banner = imageUrl;
        this.title = title;
        this.time = time;
        this.entryFeeCoins = entryFeeCoins;
        this.entryFeeTickets = entryFeeTickets;
        this.entryType = entryType;
        this.prizePool = prizePool;
        this.perKill = perKill;
        this.type = type;
        this.version = version;
        this.map = map;
        this.slots = slots;
        this.filledPositions = filledPositions;
        this.noOfPlayer = noOfPlayer;
        this.matchStatus = matchStatus; // 👈 SET VALUE
        this.matchUrl = matchUrl;
        this.userJoined = userJoined;
    }

    // Getters
    public int getId() { return id; }
    public int getImageResource() { return imageResource; }
    public String getTitle() { return title; }
    public String getTime() { return time; }
    public int getPrizePool() { return prizePool; }
    public int getPerKill() { return perKill; }
    public int getEntryFee() { return entryFee; }
    public String getType() { return type; }
    public String getVersion() { return version; }
    public String getMap() { return map; }
    public String getSlots() { return slots; }
    public String getImageUrl() { return banner; }
    public int getFilledPositions() { return filledPositions; }
    public int getNoOfPlayer() { return noOfPlayer; }
    public int getEntryFeeCoins() { return entryFeeCoins; }
    public int getEntryFeeTickets() { return entryFeeTickets; }
    public String getEntryType() { return entryType; }

    public int getMatchStatus() { return matchStatus; } // 👈 NEW GETTER
    public String getMatchUrl() {return matchUrl;}
    public String getUserJoined() {return userJoined;}
}
