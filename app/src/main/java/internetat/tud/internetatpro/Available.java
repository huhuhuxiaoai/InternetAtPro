package internetat.tud.internetatpro;

public interface Available {
    void updateTankLevels(float level1, float level2, float level3);

    void updateCapacitiveSensorStates(boolean ll1, boolean ll2, boolean ll3,
                                      boolean lh1, boolean lh2, boolean lh3);

    void updatePumpingState(boolean pumping, int tankA, int tankB);

    void setNoConnectionOverlayVisibility(int visibility);

    void hideStartOverlay();

    void setLoadingOverlayVisibility(int visibility);
}
