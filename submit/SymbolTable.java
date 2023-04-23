package submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import submit.ast.VarType;

/*
 * Code formatter project
 * CS 4481
 */
/**
 *
 */
public class SymbolTable {

  private final HashMap<String, SymbolInfo> table;
  private SymbolTable parent;
  private final List<SymbolTable> children;

  private int offset;

  public static int LABEL_IDENTIFIER = 0;

  public static String nextId() {
    return Integer.toString(SymbolTable.LABEL_IDENTIFIER++);
  }

  public SymbolTable() {
    offset = 0;
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();

    this.addGlobalSymbols();
  }

  public List<String> symbolNames() { return new ArrayList<>(table.keySet()); }

  public void addGlobalSymbols() {
    SymbolInfo println = new SymbolInfo("println", null, true);
    this.addSymbol("println", println);
  }

  public void addSymbol(String id, SymbolInfo symbol) { table.put(id, symbol); }

  public int addOffset(int n) {
    offset -= 4 * n;
    return offset;
  }

  public int getOffset() { return this.offset; }

  // Add symbols in before and reorder offsets such that symbols in before have
  // a "higher" offset
  public void addOtherTableBefore(SymbolTable before) {
    offset = 0;
    List<String> thisSymbols = symbolNames();

    for (String id : before.symbolNames()) {
      SymbolInfo symbol = before.find(id);
      if (!symbol.isFunction()) {
        addOffset(1);
        symbol.setOffset(offset);
      }
      addSymbol(id, symbol);
    }

    for (String id : thisSymbols) {
      SymbolInfo symbol = table.get(id);
      if (!symbol.isFunction()) {
        addOffset(1);
        table.get(id).setOffset(offset);
      }
    }
  }

  public int offsetOf(String id) {
    if (table.containsKey(id)) {
      return table.get(id).getOffset();
    }
    if (parent != null) {
      return -parent.getOffset() + parent.offsetOf(id);
    }
    return 0; // This shouldn't happen :D
  }

  /**
   * Returns null if no symbol with that id is in this symbol table or an
   * ancestor table.
   *
   * @param id
   * @return
   */
  public SymbolInfo find(String id) {
    if (table.containsKey(id)) {
      return table.get(id);
    }
    if (parent != null) {
      return parent.find(id);
    }
    return null;
  }

  /**
   * Returns the new child.
   *
   * @return
   */
  public SymbolTable createChild() {
    SymbolTable child = new SymbolTable();
    children.add(child);
    child.parent = this;
    return child;
  }

  public SymbolTable getParent() { return parent; }
}
