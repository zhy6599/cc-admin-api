package cc.admin.modules.jp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Data
public class JpPersonTree {

	private String id;
	private String isSon;
	private String name;
	private String wife;
	private String pic;
	private String video;
	private String pid;
	private Integer oid;
	private String live;
	private String sure;
	private String remark;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private Date born;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dead;
	private String lonlat;
	private String idCard;
	private String phone;
	private Integer sortBy;


	private List<JpPersonTree> children = new ArrayList<>();

	public JpPersonTree() {

	}
	/**
	 * 将JpPerson对象转换成JpPersonTree对象
	 * @param jpPerson
	 */
	public JpPersonTree(JpPerson jpPerson) {
		this.id = jpPerson.getId();
		this.isSon = jpPerson.getIsSon();
		this.name = jpPerson.getName();
		this.wife = jpPerson.getWife();
		this.pic = jpPerson.getPic();
		this.video = jpPerson.getVideo();
		this.pid = jpPerson.getPid();
		this.oid = jpPerson.getOid();
		this.live = jpPerson.getLive();
		this.sure = jpPerson.getSure();
		this.remark = jpPerson.getRemark();
		this.born = jpPerson.getBorn();
		this.dead = jpPerson.getDead();
		this.lonlat = jpPerson.getLonlat();
		this.idCard = jpPerson.getIdCard();
		this.phone = jpPerson.getPhone();
		this.sortBy = jpPerson.getSortBy();
	}
}
