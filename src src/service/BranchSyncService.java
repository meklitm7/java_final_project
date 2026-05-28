package service;

import model.*;
import rmi.TesfaRMIClient;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BranchSyncService {
    private static final long SYNC_INTERVAL = TimeUnit.MINUTES.toMillis(5); // Sync every 5 minutes
    private Timer syncTimer;
    private int branchId; // Unique identifier for this branch

    public BranchSyncService(int branchId) {
        this.branchId = branchId;
        this.syncTimer = new Timer(true); // Daemon thread
    }

    public void startSync() {
        syncTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Starting branch synchronization for branch " + branchId);
                    syncAllData();
                } catch (Exception e) {
                    System.err.println("Error during branch synchronization: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 0, SYNC_INTERVAL);
    }

    public void stopSync() {
        syncTimer.cancel();
        System.out.println("Branch synchronization stopped for branch " + branchId);
    }

    private void syncAllData() {
        // Sync needs
        List<Need> needs = getLocalNeeds(); // You'll need to implement this
        for (Need need : needs) {
            TesfaRMIClient.syncNeedToBranch(need);
        }

        // Sync donations
        List<Donation> donations = getLocalDonations(); // You'll need to implement this
        for (Donation donation : donations) {
            TesfaRMIClient.syncDonationToBranch(donation);
        }

        // Sync volunteers
        List<Volunteer> volunteers = getLocalVolunteers(); // You'll need to implement this
        for (Volunteer volunteer : volunteers) {
            TesfaRMIClient.syncVolunteerToBranch(volunteer);
        }

        // Sync volunteer tasks
        List<VolunteerTask> tasks = getLocalVolunteerTasks(); // You'll need to implement this
        for (VolunteerTask task : tasks) {
            TesfaRMIClient.syncVolunteerTaskToBranch(task);
        }

        // Sync task assignments
        List<VolunteerTaskAssignment> assignments = getLocalTaskAssignments(); // You'll need to implement this
        for (VolunteerTaskAssignment assignment : assignments) {
            TesfaRMIClient.syncTaskAssignmentToBranch(assignment);
        }

        System.out.println("Branch synchronization completed for branch " + branchId);
    }

    // These methods would need to be implemented to fetch local data
    // For now, we'll leave them as placeholders
    private List<Need> getLocalNeeds() {
        // Implement this to get needs from your local database
        return new java.util.ArrayList<>();
    }

    private List<Donation> getLocalDonations() {
        // Implement this to get donations from your local database
        return new java.util.ArrayList<>();
    }

    private List<Volunteer> getLocalVolunteers() {
        // Implement this to get volunteers from your local database
        return new java.util.ArrayList<>();
    }

    private List<VolunteerTask> getLocalVolunteerTasks() {
        // Implement this to get volunteer tasks from your local database
        return new java.util.ArrayList<>();
    }

    private List<VolunteerTaskAssignment> getLocalTaskAssignments() {
        // Implement this to get task assignments from your local database
        return new java.util.ArrayList<>();
    }
}