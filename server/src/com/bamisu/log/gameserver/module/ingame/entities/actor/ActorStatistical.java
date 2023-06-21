package com.bamisu.log.gameserver.module.ingame.entities.actor;

/**
 * Create by Popeye on 9:37 AM, 5/13/2020
 */
public class ActorStatistical {
    public String actor;
    public int damage;
    public int healing;
    public int damageTaken;
    public int lostBlood;
    public int dieCount;

    public ActorStatistical(String actorID) {
        actor = actorID;
        damage = 0;
        healing = 0;
        damageTaken = 0;
        lostBlood = 0;
        dieCount = 0;
    }

    public ActorStatistical(int damage, int healing, int damageTaken, int lostBlood, int dieCount) {
        this.damage = damage;
        this.healing = healing;
        this.damageTaken = damageTaken;
        this.lostBlood = lostBlood;
        this.dieCount = dieCount;
    }

    public void pushDamage(int damage){
        this.damage += damage;
    }
    public void pushHealing(int healing){
        this.healing += healing;
    }
    public void pushDamageTaken(int damageTaken){
        this.damageTaken += damageTaken;
    }
    public void pushLostBlood(int lostBlood){
        this.lostBlood += lostBlood;
    }

    public void pushDieCount(int delta) {
        this.dieCount += delta;
    }
}
