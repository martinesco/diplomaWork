package project.diploma.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.diploma.domain.entities.Role;
import project.diploma.domain.models.service.RoleServiceModel;
import project.diploma.domain.models.service.UserServiceModel;
import project.diploma.repository.RoleRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository repository, ModelMapper modelMapper) {
        this.roleRepository = repository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void seedRolesInDb() {
        if (this.roleRepository.count() == 0) {
            this.roleRepository.saveAndFlush(new Role("ROLE_USER"));
            this.roleRepository.saveAndFlush(new Role("ROLE_MODERATOR"));
            this.roleRepository.saveAndFlush(new Role("ROLE_ADMIN"));
            this.roleRepository.saveAndFlush(new Role("ROLE_ROOT"));
        }
    }

   /* @Override
    public void assignUserRoles(UserServiceModel userServiceModel, long numberOfUsers) {
        if (numberOfUsers == 0) {
            userServiceModel.setAuthorities(
                    this.roleRepository
                            .findAll()
                            .stream()
                            .map(r -> this.modelMapper.map(r, RoleServiceModel.class))
                            .collect(Collectors.toSet()));
        }
    }*/

    @Override
    public Set<RoleServiceModel> findAllRoles() {
        return this.roleRepository
                .findAll()
                .stream()
                .map(r -> this.modelMapper.map(r, RoleServiceModel.class))
                .collect(Collectors.toSet());
    }

    @Override
    public RoleServiceModel findByAuthority(String authority) {
        return this.modelMapper.map(this.roleRepository.findByAuthority(authority   ), RoleServiceModel.class);
    }
}
