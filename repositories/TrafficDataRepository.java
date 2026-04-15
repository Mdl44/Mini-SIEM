package repositories;

import java.util.List;

public interface TrafficDataRepository {
    List<String> getFirstNames();
    List<String> getDepartments();
    List<String> getAttackerIPs();
    List<String> getTargetAccounts();
    List<String> getInternalIPs();
}