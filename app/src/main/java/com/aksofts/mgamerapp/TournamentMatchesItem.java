package com.aksofts.mgamerapp;

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

    public TournamentMatchesItem(int id, String banner, String title, String time, int entryFee,
                                 int prizePool, int perKill, String type, String version,
                                 String map, String slots) {
        this.id = id;
        this.imageResource = R.drawable.ic_freefire_banner;
        this.banner = banner;
        this.title = title;
        this.time = time;
        this.entryFee = entryFee;
        this.prizePool = prizePool;
        this.perKill = perKill;
        this.type = type;
        this.version = version;
        this.map = map;
        this.slots = slots;
    }

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
}