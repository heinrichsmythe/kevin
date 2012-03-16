package org.chai.kevin.planning.budget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.planning.PlanningEntry;

public class PlanningEntryBudget extends PlanningEntry {

	private Map<PlanningCost, BudgetCost> budgetCosts;

	public PlanningEntryBudget(Map<PlanningCost, BudgetCost> budgetCosts) {
		this.budgetCosts = budgetCosts;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}

	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}

	protected Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType().equals(costType)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	public Double getGroupTotal(PlanningCostType type, String groupSection) {
		Double result = 0d;
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType() == type && 
				(	planningCost.getGroupSection() == groupSection 
					||
					planningCost.getGroupSection().equals(groupSection)
				)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	public List<String> getGroupSections(PlanningCostType type) {
		List<String> result = new ArrayList<String>();
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType().equals(type) && !result.contains(planningCost.getGroupSection())) {
				result.add(planningCost.getGroupSection());
			}
		}
		return result;
	}
	
	public List<BudgetCost> getBudgetCosts(PlanningCostType type, String groupSection) {
		List<BudgetCost> result = new ArrayList<BudgetCost>();
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (	planningCost.getType() == type 
					&& 
					(	planningCost.getGroupSection() == groupSection 
						||
						planningCost.getGroupSection().equals(groupSection)
					)
			) {
				if (getBudgetCost(planningCost) != null) result.add(getBudgetCost(planningCost));
			}
		}
		return result;
	}
	
	public List<PlanningCost> getPlanningCosts() {
		return type.getPlanningCosts(getDiscriminatorValue().getStringValue());
	}
	
	protected BudgetCost getBudgetCost(PlanningCost planningCost) {
		return budgetCosts.get(planningCost);
	}

}