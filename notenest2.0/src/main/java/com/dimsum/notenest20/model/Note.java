//package com.dimsum.notenest20.model;
//
//import jakarta.persistence.*;
//
//@Entity
//public class Note {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	private String title;
//	private String stream;
//	private String university;
//	private String year;
//	private String uploadedBy;
//
//	@Lob
//	@Column(columnDefinition = "LONGBLOB")
//	private byte[] fileData; // <-- stores PDF bytes directly
//
//	private String fileName; // optional, for download/display
//
//	// Getters and Setters
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String getStream() {
//		return stream;
//	}
//
//	public void setStream(String stream) {
//		this.stream = stream;
//	}
//
//	public String getUniversity() {
//		return university;
//	}
//
//	public void setUniversity(String university) {
//		this.university = university;
//	}
//
//	public String getYear() {
//		return year;
//	}
//
//	public void setYear(String year) {
//		this.year = year;
//	}
//
//	public byte[] getFileData() {
//		return fileData;
//	}
//
//	public void setFileData(byte[] fileData) {
//		this.fileData = fileData;
//	}
//
//	public String getFileName() {
//		return fileName;
//	}
//
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}
//
//	public String getUploadedBy() {
//		return uploadedBy;
//	}
//
//	public void setUploadedBy(String uploadedBy) {
//		this.uploadedBy = uploadedBy;
//	}
//}













// Updated code..








package com.dimsum.notenest20.model;

import jakarta.persistence.*;

@Entity
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String stream;
	private String university;
	private String year;
	private String uploadedBy;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] fileData; // main note

	private String fileName;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] contentsFileData; // contents/topics

	private String contentsFileName;

	// Getters and Setters
	// -- main fields
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	// -- main note file
	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// -- contents file
	public byte[] getContentsFileData() {
		return contentsFileData;
	}

	public void setContentsFileData(byte[] contentsFileData) {
		this.contentsFileData = contentsFileData;
	}

	public String getContentsFileName() {
		return contentsFileName;
	}

	public void setContentsFileName(String contentsFileName) {
		this.contentsFileName = contentsFileName;
	}
}
