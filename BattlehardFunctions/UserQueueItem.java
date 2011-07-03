
package BattlehardFunctions;

import java.util.UUID;

public class UserQueueItem {
private UUID id;
UUID bid;
private long cost[];
private int AUtoBuild;
int AUNumber, currTicks, ticksPerUnit, townsAtTime, originalAUAmt, totalNumber;
// inherently, deleteMe is false and is used by player to detect that this queue item should
// be deleted on the db as opposed to updated. If it's loaded in, it's clearly not over yet!
	public UserQueueItem(UUID id, UUID bid, int AUtoBuild, int AUNumber, int currTicks, int ticksPerUnit, long cost[],int townsAtTime, int originalAUAmt, int totalNumber) {
		this.id=id;this.bid=bid;this.AUtoBuild=AUtoBuild;this.AUNumber=AUNumber;this.currTicks=currTicks;
		this.ticksPerUnit=ticksPerUnit;this.cost=cost;
		this.townsAtTime=townsAtTime;
		this.originalAUAmt=originalAUAmt;
		this.totalNumber=totalNumber;
	}
	
	
	public void removeUnit() {
		AUNumber--;
	}
	public UUID getId() {
		return id;
	}
	/**
	 * Get the number of towns you had when you made this queue item.
	 * @return
	 */
	public int getTownsAtTime() {
		return townsAtTime;
	}
	public long[] getCost() {
		return cost;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	/**
	 * Get the total number of this AU that existed at the time of the queue creation.
	 * @return
	 */
	public int getOriginalAUAmt() {
		return originalAUAmt;
	}
	public void incrTicks() {
		currTicks++;
	}
	public void decrTicks() {
		currTicks--;
	}
	public int returnNumLeft() {
		return AUNumber;
	}
	public int returnTicks() {
		return currTicks;
	}
	public void resetTicks() {
		currTicks=0;
	}
	public int returnTicksPerUnit() {
		return ticksPerUnit;
	}
	public int returnTotalTicksLeft() {
		// returns the amount of ticks till the queue item would be complete if it's on the top of the stack.
		// However this is only if it's currently working, it doesn't
		// account for other queue items in front of it.
		return ticksPerUnit*AUNumber-currTicks;
		// AUNumber * ticksPerUnit gives the number left if something
		// had just started building, by subtracting current ticks, we get
		// current amount.
	}
	public boolean isDone() {
		if(AUNumber<=0) return true;
		else return false;
	}
	
	public UUID returnBID() {
		return bid;
	}
	public int returnAUtoBuild() {
		return AUtoBuild;
	}
}
