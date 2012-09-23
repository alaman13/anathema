package net.sf.anathema.lib.gui.dialog.widgets;

import java.util.ArrayList;
import java.util.List;

public class TextContent {

  private final List<TextBlock> textBlocks = new ArrayList<>();

  public void setTextBlocks(List<TextBlock> textBlocks) {
    this.textBlocks.clear();
    this.textBlocks.addAll(textBlocks);
  }

  public TextPosition getLastTextPosition() {
    if (isEmpty()) {
      return null;
    }
    int lastBlockIndex = textBlocks.size() - 1;
    return new TextPosition(lastBlockIndex, textBlocks.get(lastBlockIndex).text.length());
  }

  public int getBlockCount() {
    return textBlocks.size();
  }

  public TextBlock getBlock(int blockIndex) {
    return textBlocks.get(blockIndex);
  }

  public boolean isEmpty() {
    return textBlocks.isEmpty();
  }

  public String getText(TextPosition start, TextPosition end) {
    StringBuilder builder = new StringBuilder();
    for (int blockIndex = start.getBlockIndex(); blockIndex <= end.getBlockIndex(); ++blockIndex) {
      TextBlock block = getBlock(blockIndex);
      if (blockIndex == start.getBlockIndex() && blockIndex == end.getBlockIndex()) {
        builder.append(block.text.subSequence(start.getIndexInBlock(), end.getIndexInBlock()));
      }
      else if (blockIndex == start.getBlockIndex()) {
        builder.append(block.text.substring(start.getIndexInBlock()));
        builder.append(block.delimiter.getString());
      }
      else if (blockIndex == end.getBlockIndex()) {
        builder.append(block.text.subSequence(0, end.getIndexInBlock()));
      }
      else {
        builder.append(block.text);
        builder.append(block.delimiter.getString());
      }
    }
    return builder.toString();
  }
}