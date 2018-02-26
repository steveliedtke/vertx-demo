package de.steveliedtke.mittelvergabe.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MittelvergabeAlgorithm {
	
	private static Logger logger = LoggerFactory.getLogger(MittelvergabeAlgorithm.class);

	public void sortAndCalculateProjects(Double availableMoney, List<Project> projects) {

		double money = availableMoney.doubleValue();
		boolean pssReached = false;
		Map<Integer, List<Project>> projectAmountMap = new HashMap<>();
		final Set<Integer> amountSet = new HashSet<>();
		for (int i = 0; i < projects.size(); i++) {
			Project project = projects.get(i);
			Integer amount = Integer.valueOf(project.getVotes());
			List<Project> projectList = projectAmountMap.get(amount);
			if (projectList == null) {
				projectList = new ArrayList<>();
				projectAmountMap.put(amount, projectList);
			}
			projectList.add(project);
			amountSet.add(amount);
		}

		List<Integer> amountList = amountSet.stream().collect(Collectors.toList());
		for (int i = 0; i < amountList.size(); i++) {
			List<Project> projectList = projectAmountMap.get(amountList.get(0));
			if (pssReached) {
				projectList.forEach(project -> {
					project.setMoneyAdded(0);
				});
			}
			// TODO-dragondagda: continue here
		}

		for (int i = 0; i < projects.size(); i++) {
			Project project = projects.get(i);
			if (pssReached) {
				project.setMoneyAdded(0);
			} else if ("Sammelfach".equals(project.getName())) {
				project.setMoneyAdded(money);
				money = 0;
				pssReached = true;
			} else if (project.getMoneyAlready() + money >= project.getMoneyNeeded()) {
				double moneyAdded = project.getMoneyNeeded() - project.getMoneyAlready();
				project.setMoneyAdded(moneyAdded);
				money = money - moneyAdded;
			} else {
				project.setMoneyAdded(0.5 * money);
				money *= 0.5;
			}

			logger.debug("name: " + project.getName());
			logger.debug("votes: " + project.getVotes());
			logger.debug("moneyAdded: " + project.getMoneyAdded());
			logger.debug("MONEY LEFT: " + money + "\n");

		}
	}
}
