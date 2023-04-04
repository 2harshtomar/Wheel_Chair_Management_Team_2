package com.wcm.demo.services;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wcm.demo.dto.SsrResponseDto;
import com.wcm.demo.enums.SsrStatusEnum;
import com.wcm.demo.enums.StaffStatusEnum;
import com.wcm.demo.model.PassengerDetails;
import com.wcm.demo.model.Ssr;
import com.wcm.demo.model.Staff;
import com.wcm.demo.model.Wheel_Chair;
import com.wcm.demo.repository.SsrRepository;
import com.wcm.demo.repository.StaffRepository;

@Service
public class SsrService {
	
	@Autowired
	private SsrRepository ssrrepository;
	
	@Autowired
	private StaffRepository staffrepository;

		//Add SSR
	public Ssr addSsr(Ssr ssr) {
		PassengerDetails pssengerDetails = ssr.getPssengerDetails();
		
		Staff staff = ssr.getStaff();
		List<Staff> list = staffrepository.findAll();
		for(Staff s : list) {
			if(s.getStatus() == StaffStatusEnum.AVAILABLE) {
				s.setStatus(StaffStatusEnum.NOTAVIALABLE);
				ssr.setStaff(staff);
				break;
			}
		}
		
		Wheel_Chair wheelchair = ssr.getWheelChair();
		//Needs status of wheel chair so  if its free we can assign it to the ssr
		
		ssr.setPssengerDetails(pssengerDetails);
		
		ssr.setStatus(SsrStatusEnum.INPROGRESS);
		ssr.setWheelChair(wheelchair);
		ssr.setOpenDateTime(LocalDateTime.now());
		ssr = ssrrepository.save(ssr);
		return ssr;
	}
	

	//read all SSR request
	public List<SsrResponseDto> getallssr(){
		List<Ssr> list = ssrrepository.findAll();
		List<SsrResponseDto> listDto = new ArrayList<>();
		for(Ssr s : list) {
			SsrResponseDto dto = new SsrResponseDto();
			dto.setId(s.getId());
			dto.setPname(s.getPssengerDetails().getName());
			dto.setPcontcat(s.getPssengerDetails().getContact());
			dto.setAirCraftName(s.getPssengerDetails().getFlightDetails().getAirCraftName());
			dto.setSource(s.getPssengerDetails().getFlightDetails().getSource());
			dto.setDestination(s.getPssengerDetails().getFlightDetails().getDestination());
			dto.setFlightNo(s.getPssengerDetails().getFlightDetails().getFlightNo());
			dto.setFromDateTime(s.getPssengerDetails().getFlightDetails().getFromDateTime());
			dto.setToDateTime(s.getPssengerDetails().getFlightDetails().getToDateTime());
			dto.setStatus(s.getStatus());
			
			listDto.add(dto);
		}
		return listDto;
		
	}
	
	
	//Update SSR
	
	//Delete SSR
	public ResponseEntity<String> deleteCompletedSsr(){
		 Optional<Ssr> optional = ssrrepository.FindByStatus(SsrStatusEnum.COMPLETE);
		 if(optional.isPresent()) {
			 Ssr ssr = ssrrepository.deleteByStatus();
			 ssr.getStaff().setStatus(StaffStatusEnum.AVAILABLE);
			 ssr = ssrrepository.save(ssr);
		 }
		return ResponseEntity.status(HttpStatus.OK).body("All Completed Ssr Deleted Sucessfully");
		
	}

}