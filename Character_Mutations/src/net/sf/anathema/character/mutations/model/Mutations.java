package net.sf.anathema.character.mutations.model;

import net.sf.anathema.character.generic.rules.IExaltedSourceBook;

import java.util.ArrayList;
import java.util.List;

public class Mutations {

  private final List<IMutation> mutations = new ArrayList<>();

  public void add(String id, MutationType type) {
    mutations.add(new Mutation(id, type));
  }

  public void add(String id, MutationType type, IExaltedSourceBook book, int page) {
    mutations.add(new Mutation(id, type, book, page));
  }

  public List<IMutation> asList() {
    return mutations;
  }
}