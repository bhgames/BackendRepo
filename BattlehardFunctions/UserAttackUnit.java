package BattlehardFunctions;

import BHEngine.AttackUnit;


public class UserAttackUnit {

private String originalPlayer;
private int hp=0,attackDamage=0,attackType=1,originalPlayerID,originalTID,originalSlot,slot,support,lvl,size;
private double armor=0,armorType=0,speed=0,cargo=0,expmod=1,type=0;

public int getOriginalPlayerID() {
	return originalPlayerID;
}
public void setOriginalPlayerID(int originalPlayerID) {
	this.originalPlayerID = originalPlayerID;
}
public String getOriginalPlayer() {
	return originalPlayer;
}
public void setOriginalPlayer(String originalPlayer) {
	this.originalPlayer = originalPlayer;
}
public int getHp() {
	return hp;
}
public void setHp(int hp) {
	this.hp = hp;
}
public int getAttackDamage() {
	return attackDamage;
}
public void setAttackDamage(int attackDamage) {
	this.attackDamage = attackDamage;
}
public int getAttackType() {
	return attackType;
}
public void setAttackType(int attackType) {
	this.attackType = attackType;
}
public double getArmor() {
	return armor;
}
public void setArmor(double armor) {
	this.armor = armor;
}
public double getArmorType() {
	return armorType;
}
public void setArmorType(double armorType) {
	this.armorType = armorType;
}
public double getSpeed() {
	return speed;
}
public void setSpeed(double speed) {
	this.speed = speed;
}
public double getCargo() {
	return cargo;
}
public void setCargo(double cargo) {
	this.cargo = cargo;
}
public double getExpmod() {
	return expmod;
}
public void setExpmod(double expmod) {
	this.expmod = expmod;
}
public double getType() {
	return type;
}
public void setType(double type) {
	this.type = type;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
private String name;
public UserAttackUnit(String name, int slot, int originalPlayerID, int originalSlot,
		int originalTID,
		int support, String originalPlayer, int lvl,int size) {
	this.originalPlayer=originalPlayer;
	
	this.originalPlayerID = originalPlayerID;
	this.setOriginalSlot(originalSlot);
	this.setOriginalTID(originalTID);
	this.setLvl(lvl);
	this.size=size;
	this.setSlot(slot);
	this.setSupport(support);
	this.name=name;
	AttackUnit.setValues(this);
}
public void setLvl(int lvl) {
	this.lvl = lvl;
}
public int getLvl() {
	return lvl;
}
public void setOriginalSlot(int originalSlot) {
	this.originalSlot = originalSlot;
}
public int getOriginalSlot() {
	return originalSlot;
}
public void setOriginalTID(int originalTID) {
	this.originalTID = originalTID;
}
public int getOriginalTID() {
	return originalTID;
}
public void setSlot(int slot) {
	this.slot = slot;
}
public int getSlot() {
	return slot;
}
public void setSupport(int support) {
	this.support = support;
}
public int getSupport() {
	return support;
}
public void setSize(int size) {
	this.size = size;
}
public int getSize() {
	return size;
}


}
