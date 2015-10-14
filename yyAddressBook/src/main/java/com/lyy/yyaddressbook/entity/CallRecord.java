package com.lyy.yyaddressbook.entity;

public class CallRecord {

	public String id;
	public String number;
	public long date;
	public long duration;
	public int type;
	public String name;
	public String location;

	@Override
	public String toString() {
		return "CallRecord [id=" + id + ", number=" + number + ", date=" + date
				+ ", duration=" + duration + ", type=" + type + ", name="
				+ name + ", location=" + location + "]";
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof CallRecord) {
			CallRecord record = (CallRecord) o;
			if (record.number.equals(number)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
