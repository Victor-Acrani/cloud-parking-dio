package br.com.acrani.cloudparking.services;

import br.com.acrani.cloudparking.dto.ParkingDtoRequest;
import br.com.acrani.cloudparking.models.Parking;
import br.com.acrani.cloudparking.repositories.ParkingRepository;
import br.com.acrani.cloudparking.services.Exception.DuplicatedDataException;
import br.com.acrani.cloudparking.services.Exception.ResourceNotFoundException;
import br.com.acrani.cloudparking.services.interfaces.IParkingService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService implements IParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public List<Parking> findAll(){
        return parkingRepository.findAll();
    }

    public Parking findById(Long id){
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Parking save(ParkingDtoRequest parkingDtoRequest){

        //verificar se carro já está cadastrado
        Optional<Parking> byLicense = parkingRepository.findByLicense(parkingDtoRequest.getLicense());
        if(byLicense.isPresent()){
            throw new DuplicatedDataException("License already registered! Lincese:" + parkingDtoRequest.getLicense());
        }

        // salvar carro
        Parking parking = new Parking();
        BeanUtils.copyProperties(parkingDtoRequest, parking);
        parking.setEntryDate(LocalDateTime.now());
        parkingRepository.save(parking);
        return parking;
    }

    public Parking update(Long id, ParkingDtoRequest parkingDtoRequest){
        Parking parking = findById(id);
        BeanUtils.copyProperties(parkingDtoRequest, parking);
        parkingRepository.save(parking);
        return parking;
    }

    public void delete(Long id){
        try{
            parkingRepository.deleteById(id);
        }  catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException(id);
        }
    }

}
