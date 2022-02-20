package j4ml.extratrees;

import java.util.Set;

import j4ml.extratrees.AbstractTrees.CutResult;

public class TaskCutResult extends CutResult {
	Set<Integer> leftTasks;
	Set<Integer> rightTasks;
}
