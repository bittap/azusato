package com.my.azusato.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "celebration")
@SuperBuilder
@NoArgsConstructor
public class CelebrationSummaryEntity extends CelebrationEntity {}
