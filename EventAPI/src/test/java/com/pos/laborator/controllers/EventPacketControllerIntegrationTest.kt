//package com.pos.laborator.controllers
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.pos.laborator.model.Packet
//import com.pos.laborator.services.PacketService
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.http.MediaType
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class EventPacketControllerIntegrationTest {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private lateinit var objectMapper: ObjectMapper
//
//    @MockBean
//    private lateinit var packetService: PacketService
//
//    @Test
//    fun `when GET event-packets endpoint is called, it should return 200 OK`() {
//        mockMvc.perform(get("/api/event-manager/event-packets"))
//            .andExpect(status().isOk)
//    }
//
//    @Test
//    fun `given a packet exists, when GET event-packet by ID is called, it should return 200 OK`() {
//        val packetId = 1
//        // Given: The service will return a packet for this ID
//        // val packet = Packet(id = packetId, ...)
//        // Mockito.whenever(packetService.getPacket(packetId)).thenReturn(packet)
//
//        mockMvc.perform(get("/api/event-manager/event-packets/{id}", packetId))
//            .andExpect(status().isOk)
//    }
//
//    @Test
//    fun `given a non-existent packet ID, when GET event-packet by ID is called, it should return 404 Not Found`() {
//        val nonExistentPacketId = 999
//        // Given: The service will throw an exception for this ID
//        // Mockito.whenever(packetService.getPacket(nonExistentPacketId)).thenThrow(NoSuchElementException())
//
//        mockMvc.perform(get("/api/event-manager/event-packets/{id}", nonExistentPacketId))
//            .andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `when POST a new valid packet, it should return 201 Created and Location header`() {
//        val newPacket = Packet(id = null, ownerId = 1, name = "VIP Package", location = "Arena", description = "All inclusive")
//
//        // You would mock the service response
//        // val savedPacket = newPacket.copy(id = 1)
//        // Mockito.whenever(packetService.addPacket(any(Packet::class.java))).thenReturn(savedPacket)
//
//        mockMvc.perform(post("/api/event-manager/event-packets")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(newPacket)))
//            .andExpect(status().isCreated)
//            .andExpect(header().exists("Location"))
//    }
//
//    @Test
//    fun `when DELETE an existing packet, it should return 200 OK`() {
//        val packetId = 1
//
//        // Mock the service layer to handle the deletion
//        // Mockito.doNothing().whenever(packetService).deletePacket(packetId)
//
//        // You might need to mock the getPacket call as well, since the controller fetches it before deleting
//        // val packet = Packet(id = packetId, ...)
//        // Mockito.whenever(packetService.getPacket(packetId)).thenReturn(packet)
//
//        mockMvc.perform(delete("/api/event-manager/event-packets/{id}", packetId))
//            .andExpect(status().isOk)
//    }
//}
