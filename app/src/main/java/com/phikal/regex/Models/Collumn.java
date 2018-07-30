package com.phikal.regex.Models;

import java.util.List;

public interface Collumn {
   String getHeader();
   List<? extends Word> getWords();
}
