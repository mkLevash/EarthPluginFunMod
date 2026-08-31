package earthrp.battle;

import earthrp.customEnums.UnitTech.UnitType;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.ArmyUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleUnit extends ArmyUnit {

    public BattleUnit(ArmyUnit unit) {
        super(unit.getTech(),unit.getUniqueId(),unit.getArmyId(),unit.serializeData());
        this.setHp(unit.getHp());
        this.setMorale(unit.getMorale());
        this.setMaxMorale(unit.getMaxMorale());
        this.setDisc(unit.getDisc());


        this.setType(unit.getType());
        this.setLvl(unit.getLvl());

        switch (getType()){
            case INF ->{
                this.setFire(unit.getFire() + unit.getArmy().getOwner().getAttribute(EPlayerAttribute.INF_FIRE));
                this.setShock(unit.getShock()+ unit.getArmy().getOwner().getAttribute(EPlayerAttribute.INF_SHOCK));
            }
            case CAV ->{
                this.setFire(unit.getFire() + unit.getArmy().getOwner().getAttribute(EPlayerAttribute.CAV_FIRE));
                this.setShock(unit.getShock()+ unit.getArmy().getOwner().getAttribute(EPlayerAttribute.CAV_SHOCK));
            }
            case ART ->{
                this.setFire(unit.getFire() + unit.getArmy().getOwner().getAttribute(EPlayerAttribute.ART_FIRE));
                this.setShock(unit.getShock()+ unit.getArmy().getOwner().getAttribute(EPlayerAttribute.ART_SHOCK));
            }
        }


        this.setPipsFire(unit.getPipsFire());
        this.setPipsShock(unit.getPipsShock());
        this.setPipsMorale(unit.getPipsMorale());

        this.defFirePips = unit.getPipsFire();
        this.offFirePips = defFirePips;

        this.defShockPips = unit.getPipsShock();
        this.offShockPips = defShockPips;

        this.defMoralePips = unit.getPipsMorale();
        this.offMoralePips = defMoralePips;

        this.cavDebuff = 0.0;
        this.cavRatio = unit.getArmy().getCavRatio();



        this.setArmyId(unit.getArmyId());

        this.status = "reserve";

        if(this.getTech().getType() == UnitType.INF) fr = 1;
        else if(this.getTech().getType() == UnitType.CAV) fr = 2;
        else fr = 3;
        fr += getLvl()/2;

    }
    private double maxMorale;
    private String status;
    private int rowIndex;
    private int fr;

    private int offFirePips;
    private int defFirePips;

    private int offShockPips;
    private int defShockPips;

    private int offMoralePips;
    private int defMoralePips;

    private double cavDebuff;
    private double cavRatio;

    public double getDisciple(){
        return getArmy().getDisc()+this.getDisc();
    }

    public void dealCas(int amount){
        this.setHp(Math.max(0,getHp()-amount));
    }

    public void dealMCas(double amount){
        this.setMorale(Math.max(0, Tools.round(getMorale()-amount)));
    }

    public double getCA(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.fromString(this.getTech().getType()+"_CombatAbility"));
    }

    @Override
    public String toString() {
        return  "[" + getRowIndex() + "]" + getTech() + "{" + getMorale() + "}";
    }

    public double getFireDamage(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.FIRE_DAMAGE);
    }
    public double getFireReceived(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.FIRE_RESIST);
    }

    public double getShockDamage(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.SHOCK_DAMAGE);
    }
    public double getShockReceived(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.SHOCK_RESIST);
    }

    public double getMoraleDamage(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.MORALE_DAMAGE);
    }
    public double getMoraleReceived(){
        return getArmy().getOwner().getAttribute(EPlayerAttribute.MORALE_RESIST);
    }

}
