package armory.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character extends BaseUUIDEntity {

    @JoinColumn(name = "owner_account_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    @Column
    private String characterClass;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double strength;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double agility;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double intellect;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double stamina;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double spirit;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double armorPenetration;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double attackSpeed;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double attackPower;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double criticalStrikeChance;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double hitChance;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double spellPower;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double healingPower;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double spellCriticalChance;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double spellHitChance;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double manaPer5;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double spellPenetration;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double armor;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double block;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double dodge;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private double parry;
}
