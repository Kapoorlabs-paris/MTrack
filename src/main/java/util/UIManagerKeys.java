package util;

import java.util.Enumeration;
import javax.swing.UIManager;

public class UIManagerKeys {

  public static void main(String[] args) {
    printUIManagerKeys("Back");
  }

  private static void printUIManagerKeys(String filter) {

    String filterToLowerCase = filter.toLowerCase();

    Enumeration<?> keys = UIManager.getDefaults().keys();

    while (keys.hasMoreElements()) {

      Object key = keys.nextElement();
      String keyToString = key.toString().toLowerCase();

      if (filter != null && keyToString.contains(filterToLowerCase)) {
        System.out.println(key + " ( " + UIManager.getDefaults().get(key) + " )");
      }
    }
  }
}
