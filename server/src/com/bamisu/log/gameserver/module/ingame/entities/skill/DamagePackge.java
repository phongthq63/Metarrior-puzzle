package com.bamisu.log.gameserver.module.ingame.entities.skill;

import com.bamisu.log.gameserver.module.skill.template.active.DamageDesc;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:58 PM, 3/3/2020
 */
public class DamagePackge {
    List<Damage> damages = new ArrayList<>();

    public DamagePackge() {
    }

    public DamagePackge(List<Damage> list) {
        this.damages = list;
    }

    public List<Damage> getDamages() {
        return damages;
    }

    public void setDamages(List<Damage> damages) {
        this.damages = damages;
    }

    public DamagePackge push(Damage damage){
        damages.add(damage);
        return this;
    }

    public DamagePackge push(List<Damage> damages){
        damages.addAll(damages);
        return this;
    }

    public DamagePackge push(DamagePackge damagePackge){
        damages.addAll(damagePackge.damages);
        return this;
    }

    public DamagePackge cloneNew() {
        DamagePackge damagePackge = new DamagePackge();
        for(Damage damage : damages){
            damagePackge.push(damage.cloneNew());
        }

        return damagePackge;
    }

    //tăng damage theo tỉ lệ
    public void applyRate(double rate){
        for(Damage damage : damages){
            damage.setValue((int) Math.floor(damage.getValue() * rate));
        }
    }

    public DamagePackge share(float tankRate) {
        List<Damage> shareDamageList = new ArrayList<>();
        List<Damage> newDamageList = new ArrayList<>();
        for(Damage damage : damages){
            shareDamageList.add(new Damage(Math.round(damage.value * tankRate / 100), damage.type));
            newDamageList.add(new Damage(Math.round(damage.value * (100 - tankRate) / 100), damage.type));
        }
        this.damages = newDamageList;
        return new DamagePackge(shareDamageList);
    }

    public boolean haveDamage(){
        return !damages.isEmpty();
    }

    /**
     * giảm damage theo tỉ lệ %
     * @param rate
     */
    public void deDamage(float rate) {
        for(Damage damage : damages){
            damage.setValue((int) Math.floor(damage.getValue() * (100 - rate) / 100));
        }
    }

    public int total(){
        int total = 0;
        for(Damage damage : damages){
            total += damage.value;
        }
        return total;
    }
}
