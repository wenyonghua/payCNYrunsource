package me.zohar.runscore.agent.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invite_code_channel_rebate")
@DynamicInsert(true)
@DynamicUpdate(true)
public class InviteCodeChannelRebate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String channelId;

	private Double rebate;

	@Column(name = "invite_code_id", length = 32)
	private String inviteCodeId;

}
