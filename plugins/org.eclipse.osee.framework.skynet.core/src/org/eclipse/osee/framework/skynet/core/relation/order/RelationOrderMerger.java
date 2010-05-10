/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationOrderMerger<T> {
	private List<T> starredList;

	public RelationOrderMerger() {
		starredList = new ArrayList<T>();
	}

	public List<T> computeMergedOrder(List<T> leftOrder, List<T> rightOrder,
			Collection<T> mergedSet) {
		makeSubset(leftOrder, mergedSet);
		makeSubset(rightOrder, mergedSet);

		starUnionComplement(leftOrder, rightOrder);

		return cursorAlgorithm(leftOrder, rightOrder, mergedSet);
	}

	private void makeSubset(List<T> subset, Collection<T> superset) {
		for (int i = 0; i < subset.size(); i++) {
			T current = subset.get(i);
			if (!superset.contains(current)) {
				subset.remove(i);
				i--;
			}
		}
	}

	private void starUnionComplement(Collection<T> setA, Collection<T> setB) {
		for (T element : setA) {
			if (!setB.contains(element)) {
				addStar(element);
			}
		}

		for (T element : setB) {
			if (!setA.contains(element)) {
				addStar(element);
			}
		}
	}

	private List<T> cursorAlgorithm(List<T> left, List<T> right,
			Collection<T> mergedSet) {
		List<T> mergedOrder = new ArrayList<T>();
		int leftIndex = 0;
		int rightIndex = 0;
		while (leftIndex < left.size() && rightIndex < right.size()) {
			T leftElement = left.get(leftIndex);
			T rightElement = right.get(rightIndex);
			boolean resolved = false;
			if (leftElement.equals(rightElement)) {
				mergedOrder.add(leftElement);
				leftIndex++;
				rightIndex++;
				resolved = true;
			}
			if (hasStar(leftElement)) {
				mergedOrder.add(leftElement);
				leftIndex++;
				resolved = true;
			}
			if (hasStar(rightElement)) {
				mergedOrder.add(rightElement);
				rightIndex++;
				resolved = true;
			}
			if (!resolved)
				return null;
		}

		return mergedOrder;
	}

	private void addStar(T element) {
		starredList.add(element);
	}

	private boolean hasStar(T element) {
		return starredList.contains(element);
	}


}
