

GET endpoint/pokemon/												RESPONSE 200,404
Get enpoint/pokemon/{id}											RESPONSE 200,404
DELETE endpoint/pokemon/{id}										RESPONSE 200,404
POST endpoint/pokemon/												RESPONSE 201
	request															RESPONSE Body
	{																{
		"nombre : NUEVO_NOMBRE"											"id" : "X"
		"habilidad" : [] //TODO par asiguiente entrega.					"nombre" : 	"NOMBRE_NUEVO"
	}																}
																	RESPONSE 409 NOmbre duplicado, nombre min1 max 50 en la BD
	
PUT endpoint/pokemon/{id}											RESPONSE 200
	request															response Body
	{		 														{
		"id" : 3,														"id" : "3",
		"nombre : NUEVO_NOMBRE",										"nombre" : 	"NOMBRE_NUEVO"
		"habilidad" : [] //TODO par asiguiente entrega.				}
	}																209 NOmbre duplicado, nombre min1 max 50 en la BD