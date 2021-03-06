package net.sf.anathema.campaign.music.impl.model.tracks;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import net.sf.anathema.lib.exception.UnreachableCodeReachedException;

import java.io.File;

public class FileUtilities {

  private FileUtilities() {
    throw new UnreachableCodeReachedException();
  }

  public static int getFileCount(File folder, boolean recursive, Predicate<File> predicate) {
    Preconditions.checkNotNull(folder);
    Preconditions.checkArgument(folder.exists() && folder.isDirectory(), "Must be an existing folder."); //$NON-NLS-1$
    int count = 0;
    for (File file : folder.listFiles()) {
      if (predicate.apply(file)) {
        count++;
      }
      if (file.isDirectory() && recursive) {
        count += getFileCount(file, recursive, predicate);
      }
    }
    return count;
  }
}