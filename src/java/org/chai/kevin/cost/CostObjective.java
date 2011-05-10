package org.chai.kevin.cost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.chai.kevin.Objective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.hibernate.annotations.Cascade;

@Entity(name="CostObjective")
@Table(name="dhsst_cost_objective")
public class CostObjective extends Objective {

	private Integer order;
	private List<CostTarget> targets = new ArrayList<CostTarget>();

	@OneToMany(cascade=CascadeType.ALL, mappedBy="parent", targetEntity=CostTarget.class)
	@OrderBy(value="order")
	public List<CostTarget> getTargets() {
		return targets;
	}
	public void setTargets(List<CostTarget> targets) {
		this.targets = targets;
	}

	@Basic(optional=true)
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public void addTarget(CostTarget target) {
		target.setParent(this);
		targets.add(target);
		Collections.sort(targets);
	}

}