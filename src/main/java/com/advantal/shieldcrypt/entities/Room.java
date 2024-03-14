package com.advantal.shieldcrypt.entities;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;

import com.advantal.shieldcrypt.services.AvatarService;
import com.advantal.shieldcrypt.utils.LanguageUtils;
import com.advantal.shieldcrypt.utils.UIHelper;
import com.advantal.shieldcrypt.xmpp.Jid;

public class Room implements AvatarService.Avatarable, Comparable<Room> {

    public String address;
    public String name;
    public String description;
    public String language;
    public int nusers;

    public Room(String address, String name, String description, String language, int nusers) {
        this.address = address;
        this.name = name;
        this.description = description;
        this.language = language;
        this.nusers = nusers;
    }

    public Room() {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Jid getRoom() {
        try {
            return Jid.of(address);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getLanguage() {
        return LanguageUtils.convert(language);
    }

    @Override
    public int getAvatarBackgroundColor() {
        Jid room = getRoom();
        return UIHelper.getColorForName(room != null ? room.asBareJid().toEscapedString() : name);
    }

    @Override
    public String getAvatarName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equal(address, room.address) &&
                Objects.equal(name, room.name) &&
                Objects.equal(description, room.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address, name, description);
    }


    public boolean contains(String needle) {
        return Strings.nullToEmpty(name).contains(needle)
                || Strings.nullToEmpty(description).contains(needle)
                || Strings.nullToEmpty(address).contains(needle);
    }

    @Override
    public int compareTo(Room o) {
        return ComparisonChain.start()
                .compare(o.nusers, nusers)
                .compare(Strings.nullToEmpty(name), Strings.nullToEmpty(o.name))
                .compare(Strings.nullToEmpty(address), Strings.nullToEmpty(o.address))
                .result();
    }
}