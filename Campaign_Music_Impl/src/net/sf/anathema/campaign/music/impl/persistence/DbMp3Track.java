package net.sf.anathema.campaign.music.impl.persistence;

import com.db4o.ObjectContainer;
import com.db4o.types.Db4oList;
import net.sf.anathema.campaign.music.model.track.IMp3Track;
import net.sf.anathema.campaign.music.model.track.Md5Checksum;
import net.sf.anathema.campaign.music.presenter.IMusicEvent;
import net.sf.anathema.campaign.music.presenter.IMusicMood;
import net.sf.anathema.campaign.music.presenter.IMusicTheme;
import net.sf.anathema.lib.lang.StringUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DbMp3Track implements IMp3Track {

  private final String album;
  private final String artist;
  private final String track;
  private final String title;
  private final Md5Checksum checksum;
  private final List<String> fileReferences = new ArrayList<>();
  private int libraryReferenceCount = 0;
  private String givenName;
  private final Db4oList events;
  private final Db4oList themes;
  private final Db4oList moods;

  public DbMp3Track(IMp3Track track, ObjectContainer db) {
    this.album = track.getAlbum();
    this.artist = track.getArtist();
    this.title = track.getTitle();
    this.track = track.getTrack();
    this.checksum = track.getCheckSum();
    this.givenName = track.getGivenName();
    for (String reference : track.getFileReferences()) {
      addFileReference(reference);
    }
    events = db.ext().collections().newLinkedList();
    themes = db.ext().collections().newLinkedList();
    moods = db.ext().collections().newLinkedList();
  }

  @Override
  public String getAlbum() {
    return album;
  }

  @Override
  public String getArtist() {
    return artist;
  }

  @Override
  public String getTrack() {
    return track;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public Md5Checksum getCheckSum() {
    return checksum;
  }

  @Override
  public String[] getFileReferences() {
    return fileReferences.toArray(new String[fileReferences.size()]);
  }

  public void addFileReference(String reference) {
    if (!fileReferences.contains(reference)) {
      fileReferences.add(reference);
    }
  }

  @Override
  public String toString() {
    return StringUtilities.isNullOrTrimmedEmpty(getGivenName()) ? "Unknown" : getGivenName(); //$NON-NLS-1$
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DbMp3Track)) {
      return false;
    }
    DbMp3Track other = (DbMp3Track) obj;
    return other.getCheckSum().equals(checksum);
  }

  @Override
  public int hashCode() {
    return checksum.hashCode() + title.hashCode();
  }

  public void increaseLibraryReferenceCount() {
    libraryReferenceCount++;
  }

  public void decreaseLibraryReferenceCount() {
    libraryReferenceCount--;
  }

  public int getLibraryReferenceCount() {
    return libraryReferenceCount;
  }

  @Override
  public String getGivenName() {
    return givenName;
  }

  @Override
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  @Override
  @SuppressWarnings("unchecked")
  public IMusicEvent[] getEvents() {
    return (IMusicEvent[]) events.toArray(new IMusicEvent[events.size()]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setEvents(IMusicEvent[] musicMoods) {
    events.clear();
    Collections.addAll(events, musicMoods);
  }

  @Override
  @SuppressWarnings("unchecked")
  public IMusicTheme[] getThemes() {
    return (IMusicTheme[]) themes.toArray(new IMusicTheme[themes.size()]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setThemes(IMusicTheme[] themes) {
    this.themes.clear();
    Collections.addAll(this.themes, themes);
  }

  @Override
  @SuppressWarnings("unchecked")
  public IMusicMood[] getMoods() {
    return (IMusicMood[]) moods.toArray(new IMusicMood[moods.size()]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setMoods(IMusicMood[] moods) {
    this.moods.clear();
    Collections.addAll(this.moods, moods);
  }
}