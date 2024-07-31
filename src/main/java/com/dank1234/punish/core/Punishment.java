package com.dank1234.punish.core;

import com.dank1234.punish.Main;
import com.dank1234.punish.utils.Utils;
import com.dank1234.punish.utils.data.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Punishment implements Utils {
    private final Database database = Main.getDatabase();

    private String banId;
    private Type punishmentType;
    private UUID player;
    private UUID punisher;
    private String reason;
    private long length;
    private long startTime;
    private boolean silent;
    private boolean active;
    private long endTime;

    public Punishment(final String banId, final Type type, final UUID player, final UUID punisher, final String reason, final long length, final boolean silent, final boolean active) {
        this.banId = banId;
        this.punishmentType = type;
        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
        this.silent = silent;
        this.length = length;
        this.active = active;
        this.startTime = System.currentTimeMillis();
        this.endTime = length != -1L ? startTime+length : Long.MAX_VALUE;
    }
    public Punishment(final String banId, final Type type, final UUID player, final UUID punisher, final String reason, final long length, final long endTime, final boolean silent, final boolean active) {
        this.banId = banId;
        this.punishmentType = type;
        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
        this.silent = silent;
        this.length = length;
        this.active = active;
        this.startTime = System.currentTimeMillis();
        this.endTime = length != -1L ? endTime : Long.MAX_VALUE;
    }
    public Punishment(final Type type, final UUID player, final UUID punisher, final String reason, final long length, final boolean silent, final boolean active) {
        this.banId = this.generateBanId();
        this.punishmentType=type;
        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
        this.silent = silent;
        this.length = type.equals(Type.KICK) ? -1L : length;
        this.active=active;
        this.startTime=System.currentTimeMillis();
        this.endTime=length != -1L ? startTime+length : Long.MAX_VALUE;
    }
    public Punishment(final Type type, final UUID player, final UUID punisher, final String reason, final long length, final long endTime, final boolean silent, final boolean active) {
        this(type, player, punisher, reason, length, silent, active);
        this.endTime= length != -1L ? endTime : Long.MAX_VALUE;;
    }

    public String banId() {
        return banId;
    }
    public Type type() {
        return this.punishmentType;
    }
    public UUID player() {
        return this.player;
    }
    public UUID punisher() {
        return this.punisher;
    }
    public String reason() {
        return this.reason;
    }
    public long length() {
        return this.length;
    }
    public long startTime() {
        return this.startTime;
    }
    public long endTime() {
        return this.endTime;
    }
    public boolean silent() {
        return this.silent;
    }
    public boolean active() {
        return active;
    }

    public Punishment banId(final String banId) {
        this.banId = banId;
        return this;
    }
    public Punishment type(final Type type) {
        this.punishmentType = type;
        return this;
    }
    public Punishment player(final UUID player) {
        this.player = player;
        return this;
    }
    public Punishment punisher(final UUID punisher) {
        this.punisher = punisher;
        return this;
    }
    public Punishment reason(final String reason) {
        this.reason = reason;
        return this;
    }
    public Punishment length(final long length) {
        this.length = length;
        return this;
    }
    public Punishment startTime(final long startTime) {
        this.startTime = startTime;
        return this;
    }
    public Punishment endTime(final long endTime) {
        this.endTime = endTime;
        return this;
    }
    public Punishment silent(final boolean silent) {
        this.silent = silent;
        return this;
    }
    public Punishment active(final boolean active) {
        this.active = active;
        return this;
    }

    private void alert() {
        String prefix = (this.silent() ? "&8[&7&oSilent&8] " : "&8[&7&oGlobal&8] ");
        String type = switch (this.type()) {
            case KICK -> "kicked";
            case WARN -> "warned";
            case MUTE -> "muted";
            case BAN -> "banned";
            case UNWARN -> "unwarned";
            case UNMUTE -> "unmuted";
            case UNBAN -> "unbanned";
        };

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.silent()) {
                player.sendMessage(Colour(this.messageAlert(prefix, type)));
            }else {
                if (!player.hasPermission("punish.alert." + this.type().name().toLowerCase())) {
                    return;
                }
                player.sendMessage(Colour(this.messageAlert(prefix, type)));
            }
        }
    }
    private String messageAlert(String prefix, String typeParam) {
        String type = typeParam;
        String timeMsg = "";
        if (this.length() == -1L || this.endTime() == Long.MAX_VALUE) {
            type = (!Objects.equals(type, "kicked") ? "permanently " : "") + type;
            if (this.length() == -1L && (typeParam.equals("unbanned") || typeParam.equals("unmuted") || typeParam.equals("unwarned"))) {
                type = typeParam;
                timeMsg = "";
            }
        } else {
            timeMsg = " for &a" + this.formatDuration(this.length());
        }
        return prefix + "&a" + database.getNameByUUID(this.player()) + "&f has been &c" + type + "&f by &a" + database.getNameByUUID(this.punisher()) + "&f" + timeMsg + "&f with the reason &a'" + this.reason() + "'&f.";
    }

    public void punish() {
        Database database = Main.getDatabase();
        database.insertPunishment(this);
        this.alert();
    }

    @Override
    public String toString() {
        return "{\n"+
                    "\tbanId: "+this.banId()+",\n"+
                    "\ttype:"+this.type()+",\n"+
                    "\tplayer: "+this.player()+","+
                    "\tpunisher: "+this.punisher()+",\n"+
                    "\treason: "+this.reason()+",\n"+
                    "\tlength: "+this.length()+",\n"+
                    "\tstartTime: "+this.startTime()+",\n"+
                    "\tendTime: "+this.endTime()+",\n"+
                    "\tsilent: "+this.silent()+",\n"+
                    "\tactive: "+this.active()+",\n"+
                "}";
    }
}