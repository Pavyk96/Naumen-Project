package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.model.Deal;

import java.util.List;

/**
 * @author Daria
 */
public interface BreakdownStrategy {
    String getDimensionName();
    List<BreakdownData> buildBreakdown(List<Deal> deals, List<String> metrics);
}
