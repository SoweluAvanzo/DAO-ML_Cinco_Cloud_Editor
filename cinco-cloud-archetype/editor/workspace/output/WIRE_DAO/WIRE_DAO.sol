// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;
/**
 * @title WIRE_DAO
 * @notice Manage the governance of the consortium for independent media production and sharing
 */
import "./interfaces/IPermissionManager.sol";
contract WIRE_DAO is IPermissionManager {
    bool internal committee_initialization_blocked;
    mapping(address => uint32) internal roles;
    uint32[8] internal role_permissions;
    uint32[8] internal all_roles = [
        3072, // #0) Everyday_User -> ID : 0 , control bitmask: 1100000
        3073, // #1) Human_Data_Curator -> ID : 1 , control bitmask: 1100000
        7170, // #2) Professional_User -> ID : 2 , control bitmask: 11100000
        6147, // #3) AI_Curator -> ID : 3 , control bitmask: 11000000
        3076, // #4) Collaborator -> ID : 4 , control bitmask: 1100000
        3077, // #5) Super-User -> ID : 5 , control bitmask: 1100000
        2054, // #6) WIRE_DAOOwner -> ID : 6 , control bitmask: 1000000
        2055 // #7)  The_Consortium -> ID : 7 , control bitmask: 1000000
    ];
 //Events
    event RoleRevoked(address indexed user, uint32 indexed role);
    event RoleAssigned(address indexed user, uint32 indexed role);
    event PermissionGranted(uint32 indexed role, uint32 indexed permission);
    event PermissionRevoked(uint32 indexed role, uint32 indexed permission);



        modifier controlledBy(address sender, uint32 user_role_id, bool allowNullRole_user, uint32 new_role_id) {
            //we obtain the control relations of the controller role by shifting the its id by the number of bits contained in ids
            //the sender must control BOTH the target role AND the user's role

            uint32 index_new_role = new_role_id & 31;
            uint32 sender_role_index = ( uint32(1) << ( roles[sender] & 31 ) );

            require(
                ( // the new role must be a valid one
                    index_new_role < 8 // checking for "index out of bounds"
                )
                && ( // "check the sender and target user control relation"
                    (allowNullRole_user && (user_role_id == 0)) || // allow to add role if the user doesn't have one
                    ((
                        (user_role_id >> 5) // get the user role's bitmask 
                        &  // (... and then perform the bitwise-and with ...)
                        sender_role_index
                    ) != 0) // final check
                ) &&
                ( // "control relation check between sender and the target role"
                    (
                        ( all_roles[index_new_role] >> 5) // get the new role's bitmask from those internally stored
                        &  // (... and then perform the bitwise-and with ...)
                        sender_role_index
                    ) != 0 // final check
                )
                , "the given controller can't perform the given operation on the given controlled one" );
            _;
        }
        


 
    modifier hasPermission(address _executor, uint32 _permissionIndex) {
        require(role_permissions[uint32(roles[_executor] & 31)] & (uint32(1) << _permissionIndex) != 0, "User does not have this permission");
        _;
    }
            
    constructor(
) {
        role_permissions[0] = 24576; // #0) Everyday_User 

        role_permissions[1] = 0; // #1) Human_Data_Curator 

        role_permissions[2] = 28416; // #2) Professional_User 

        role_permissions[3] = 0; // #3) AI_Curator 

        role_permissions[4] = 393219; // #4) Collaborator 

        role_permissions[5] = 516096; // #5) Super-User 

        role_permissions[6] = 524287; // #6) WIRE_DAOOwner 

        role_permissions[7] = 4348; // #7) The_Consortium 

roles[msg.sender] = all_roles[6]; // WIRE_DAOOwner
}
    function initializeCommittees(address _The_Consortium) external {
        require(roles[msg.sender] == all_roles[6], "Only the owner can initialize the Dao");  // WIRE_DAOOwner
    require(committee_initialization_blocked == false && _The_Consortium != address(0), "Invalid committee initialization");
        roles[_The_Consortium] = all_roles[0]; // The_Consortium
        committee_initialization_blocked = true;
    }

        
        function canControl(uint32 controller, uint32 controlled) public pure returns(bool controls){
             // ( "CAN the sender control the target user (through its role)?"
                //(allowNullRole && (target_role_id == 0)) || // allow to add role if the user has not already one assigned to it
                if((
                    (controlled >> 5 ) // get the role's bitmask 
                    &  // (and then perform the bitwise-and with ...)
                    (uint32(1) << ( controller & 31 )) // (...) get the sender role's index AND shift it accordingly 
                ) != 0 ){
                    controls = true;
                     return controls;} else {return controls;}
        }
        
        function assignRole(address _user, uint32 _role) external controlledBy(msg.sender, roles[_user], true, _role) {
            require(_user != address(0) , "Invalid user address" );
            
            roles[_user] = _role;
            emit RoleAssigned(_user, _role);
        }

        function revokeRole(address _user, uint32 _role) external controlledBy(msg.sender, roles[_user], false, _role) {
            require(roles[_user] == _role, "User's role and the role to be removed don't coincide" );

            delete roles[_user];
            emit RoleRevoked(_user, _role);
        }

        function grantPermission(uint32 _role, uint32 _permissionIndex) external hasPermission(msg.sender, _permissionIndex) {
            require(canControl(roles[msg.sender], _role), "cannot grant permission, as the control relation is lacking");
            uint32 new_role_perm_value;
            new_role_perm_value  = role_permissions[_role & 31 ] | (uint32(1) << _permissionIndex);
            role_permissions[_role & 31 ] = new_role_perm_value;
            
            emit PermissionGranted(_role, _permissionIndex);
        }

        function revokePermission(uint32 _role, uint32  _permissionIndex) external hasPermission(msg.sender, _permissionIndex) {
            require(canControl(roles[msg.sender], _role), "cannot revoke permission, as the control relation is lacking");
            uint32 new_role_perm_value;
            new_role_perm_value = role_permissions[_role & 31] & ~(uint32(1) << _permissionIndex);
            role_permissions[_role & 31] = new_role_perm_value;

            emit PermissionRevoked(_role, _permissionIndex);
        }

        function hasRole(address user) external view returns(uint32) {
            return roles[user];
        }

        function has_permission(address user, uint32 _permissionIndex) external view returns (bool) {
            if (role_permissions[uint32(roles[user] & 31)] & (uint32(1) << _permissionIndex) != 0){ 
                return true;
            }else{
                return false;
            }
        }
             
         

        function Propose_Update_Copyright_policies() external hasPermission(msg.sender, 0) {
            // TODO: Implement the function logic here
        }
                

        function Propose_Schema_Update() external hasPermission(msg.sender, 1) {
            // TODO: Implement the function logic here
        }
                

        function Reward_Collaborator() external hasPermission(msg.sender, 2) {
            // TODO: Implement the function logic here
        }
                

        function Include_collaborating_institution() external hasPermission(msg.sender, 3) {
            // TODO: Implement the function logic here
        }
                

        function Update_voting_power_weights() external hasPermission(msg.sender, 4) {
            // TODO: Implement the function logic here
        }
                

        function remove_voting_power() external hasPermission(msg.sender, 5) {
            // TODO: Implement the function logic here
        }
                

        function Approve_Schema_Update() external hasPermission(msg.sender, 6) {
            // TODO: Implement the function logic here
        }
                

        function Remove_Collaborating_Institution() external hasPermission(msg.sender, 7) {
            // TODO: Implement the function logic here
        }
                

        function access_restricted_data() external hasPermission(msg.sender, 8) {
            // TODO: Implement the function logic here
        }
                

        function add_new_data() external hasPermission(msg.sender, 9) {
            // TODO: Implement the function logic here
        }
                

        function add_comment() external hasPermission(msg.sender, 10) {
            // TODO: Implement the function logic here
        }
                

        function consult_AI_curator() external hasPermission(msg.sender, 11) {
            // TODO: Implement the function logic here
        }
                

        function Upgrade_Smart_Contracts() external hasPermission(msg.sender, 12) {
            // TODO: Implement the function logic here
        }
                

        function Watch_content() external hasPermission(msg.sender, 13) {
            // TODO: Implement the function logic here
        }
                

        function Search_content() external hasPermission(msg.sender, 14) {
            // TODO: Implement the function logic here
        }
                

        function update_metadata() external hasPermission(msg.sender, 15) {
            // TODO: Implement the function logic here
        }
                

        function add_new_variable() external hasPermission(msg.sender, 16) {
            // TODO: Implement the function logic here
        }
                

            function canVote(address user, uint32 permissionIndex) external view returns (bool) {
                require(role_permissions[uint32(roles[user] & 31)] & (uint32(1) << permissionIndex) != 0, "User does not have this permission");
                return true;
            }

            function canPropose(address user, uint32 permissionIndex) external view returns (bool) {
                require(role_permissions[uint32(roles[user] & 31)] & (uint32(1) << permissionIndex) != 0, "User does not have this permission");
                return true;
            }
}
