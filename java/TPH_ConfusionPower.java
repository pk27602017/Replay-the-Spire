package tobyspowerhouse.powers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.powers.*;

public class TPH_ConfusionPower extends AbstractPower
{
    public static final String POWER_ID = "TPH_Confusion";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private boolean justApplied;
	private int strengthMod;
	private int lowerStrengthLimit;
	private int upperStrengthLimit;
    
    public TPH_ConfusionPower(final AbstractCreature owner) {
        this.name = ConfusionPower.NAME;
        this.ID = "TPH_Confusion";
        this.owner = owner;
        this.type = PowerType.DEBUFF;
		this.strengthMod = 0;
		this.lowerStrengthLimit = -3;
		this.upperStrengthLimit = 2;
        this.updateDescription();
        this.loadRegion("confusion");
		this.isTurnBased = false;
    }
    public TPH_ConfusionPower(final AbstractCreature owner, int amount) {
        this.name = ConfusionPower.NAME;
        this.ID = "TPH_Confusion";
        this.owner = owner;
        this.type = PowerType.DEBUFF;
		this.isTurnBased = true;
		this.amount = amount;
		if (owner.isPlayer) {
			this.justApplied = true;
		}
		this.strengthMod = 0;
		this.lowerStrengthLimit = -3;
		this.upperStrengthLimit = 2;
        this.updateDescription();
        this.loadRegion("confusion");
    }
    public TPH_ConfusionPower(final AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = ConfusionPower.NAME;
        this.ID = "TPH_Confusion";
        this.owner = owner;
        this.type = PowerType.DEBUFF;
		this.isTurnBased = true;
		this.amount = amount;
		if (owner.isPlayer && isSourceMonster) {
			this.justApplied = true;
		}
		this.strengthMod = 0;
		this.lowerStrengthLimit = -3;
		this.upperStrengthLimit = 2;
        this.updateDescription();
        this.loadRegion("confusion");
    }
	
    @Override
    public void atEndOfRound() {
		if (!this.owner.isPlayer) {
			this.flash();
			//AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -1 * this.strengthMod), -1 * this.strengthMod, true, AbstractGameAction.AttackEffect.NONE));
			//this.strengthMod = 0;
			this.strengthMod = AbstractDungeon.miscRng.random(this.upperStrengthLimit - this.lowerStrengthLimit) + this.lowerStrengthLimit;
			if (!this.isTurnBased) {
				return;
			}
			if (this.justApplied) {
				this.justApplied = false;
				return;
			}
			
			if (this.amount <= 0) {
				AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "TPH_Confusion"));
			}
			else {
				AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, "TPH_Confusion", 1));
			}
			
		}
    }
	
	@Override
    public void atEndOfTurn(final boolean isPlayer) {
		if (isPlayer) {
			if (!this.owner.isPlayer) {
				//this.flash();
				//this.strengthMod = AbstractDungeon.miscRng.random(this.upperStrengthLimit - this.lowerStrengthLimit) + this.lowerStrengthLimit;
				//AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.strengthMod), this.strengthMod, true, AbstractGameAction.AttackEffect.NONE));
			} else {
				if (!this.isTurnBased) {
					return;
				}
				if (this.justApplied) {
					this.justApplied = false;
					return;
				}
				
				if (this.amount <= 0) {
					AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "TPH_Confusion"));
				}
				else {
					AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, "TPH_Confusion", 1));
				}
				
			}
		}
    }
    
    @Override
    public float atDamageGive(final float damage, final DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage + this.strengthMod;
        }
        return damage;
    }
	
    @Override
    public void updateDescription() {
		if (this.owner.isPlayer)
			this.description = TPH_ConfusionPower.DESCRIPTIONS[0];
		else
			this.description = this.owner.name + " gets a random amount of strength between " + this.lowerStrengthLimit + " and +" + this.upperStrengthLimit + " each round. NL (Currently " + this.strengthMod + ")";
		if (this.isTurnBased) {
			this.description += " NL (Lasts " + this.amount + " more round";
			if (this.amount > 1)
				this.description += "s";
			this.description += ")";
		}
    }
	
	@Override
	public void stackPower(final int stackAmount) {
        if (this.amount == -1 || stackAmount == -1) {
            this.amount = -1;
			this.isTurnBased = false;
            return;
        }
        this.fontScale = 8.0f;
        this.amount += stackAmount;
    }
    
    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Confusion");
        NAME = TPH_ConfusionPower.powerStrings.NAME;
        DESCRIPTIONS = TPH_ConfusionPower.powerStrings.DESCRIPTIONS;
    }
}