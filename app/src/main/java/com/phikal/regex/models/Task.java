package com.phikal.regex.models;

import java.io.Serializable;
import java.util.List;

public interface Task extends Serializable {
   List<Collumn> getCollumns();
   List<Input> getInputs();
}
