package net.sf.anathema.framework.model;

import net.sf.anathema.framework.reporting.IReportRegistry;
import net.sf.anathema.framework.reporting.Report;
import net.sf.anathema.framework.repository.IItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportRegistry implements IReportRegistry {

  private final List<Report> reports = new ArrayList<>();

  public ReportRegistry() {
    super();
  }

  @Override
  public void addReports(Report... newReports) {
    Collections.addAll(this.reports, newReports);
  }

  @Override
  public Report[] getReports(IItem item) {
    List<Report> supportedReports = new ArrayList<>();
    for (Report report : reports) {
      if (report.supports(item)) {
        supportedReports.add(report);
      }
    }
    return supportedReports.toArray(new Report[supportedReports.size()]);
  }
}
